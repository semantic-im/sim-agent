/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sim.agent;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.NetStat;
import org.hyperic.sigar.ProcFd;
import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;
import org.hyperic.sigar.SigarPermissionDeniedException;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.agent.mbean.Agent;
import sim.data.SystemId;
import sim.data.SystemMetrics;
import sim.data.SystemMetricsImpl;

/**
 * This is the thread reading the system metrics. It is running periodically.
 * 
 * @author valer
 * 
 */
public class AgentThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AgentThread.class);

	private SystemId systemId;

	private Sigar sigar = null;
	private Agent agentMBean = null;

	private boolean openFileDescriptorsMetrics = true;

	private NetworkStatistics ns = new NetworkStatistics();
	private long oldNetworkSent = 0;
	private long oldNetworkReceived = 0;
	private long oldLoopbackNetworkSent = 0;
	private long oldLoopbackNetworkReceived = 0;
	private boolean firstRead = true;

	/*
	 * Initializae the agent thread
	 */
	public AgentThread(SystemId systemId) {
		this.systemId = systemId;

		sigar = new Sigar();

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName name = new ObjectName("sim.agent:type=Agent");
			agentMBean = new Agent();
			mbs.registerMBean(agentMBean, name);
		} catch (Exception e) {
			logger.error("exception thring to register mbean", e);
			throw new RuntimeException("exception thring to register mbean", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// logger.info("agent running ... ");

		Mem mem = null;
		Swap swap = null;

		long readBytes = 0;
		long writeBytes = 0;
		CpuPerc cpuPerc = null;
		Cpu cpu = null;
		ProcStat proc = null;

		try {
			mem = sigar.getMem();
			swap = sigar.getSwap();

			FileSystem[] fileSystemList = sigar.getFileSystemList();
			for (int i = 0; i < fileSystemList.length; i++) {
				String devName = fileSystemList[i].getDevName();
				// logger.info("get file system usage for filesystem : " +
				// devName);
				FileSystemUsage fileSystemUsage = null;
				try {
					fileSystemUsage = sigar.getFileSystemUsage(devName);
				} catch (SigarException e) {
					// logger.warn("could not get file system usage for device name : "
					// + devName);
					continue;
				}
				readBytes += fileSystemUsage.getDiskReadBytes();
				writeBytes += fileSystemUsage.getDiskWriteBytes();
			}

			cpuPerc = sigar.getCpuPerc();
			cpu = sigar.getCpu(); // take the whole list of cpus

			OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

			double systemLoadAverage = osMXBean.getSystemLoadAverage();
			if (systemLoadAverage < 0) {
				systemLoadAverage = -1.0;
			}
			long actualFree = getSizeInKb(mem.getActualFree());
			long actualUsed = getSizeInKb(mem.getActualUsed());
			long swapUsed = getSizeInKb(swap.getUsed());
			long openFileDescriptors = getOpenFileDescriptors();
			long swapPageIn = swap.getPageIn();
			long swapPageOut = swap.getPageOut();
			long ioRead = getSizeInKb(readBytes);
			long ioWrite = getSizeInKb(writeBytes);
			double userPerc = cpuPerc.getUser();
			double sysPerc = cpuPerc.getSys();
			double idlePerc = cpuPerc.getIdle();
			double waitPerc = cpuPerc.getWait();
			double irqPerc = cpuPerc.getIrq();
			double user = getPeriodInSec(cpu.getUser());
			double sys = getPeriodInSec(cpu.getSys());
			double idle = getPeriodInSec(cpu.getIdle());
			double wait = getPeriodInSec(cpu.getWait());
			double irq = getPeriodInSec(cpu.getIrq());

			proc = sigar.getProcStat();

			long processesCount = proc.getTotal();
			long runningProcessesCount = proc.getRunning();
			long threadsCount = proc.getThreads();

			refreshNetworkStatistics(ns);

			agentMBean.loadData(systemLoadAverage, actualFree, actualUsed, swapUsed, openFileDescriptors,
					swapPageIn, swapPageOut, ioRead, ioWrite, userPerc, sysPerc, idlePerc, waitPerc, irqPerc,
					user, sys, idle, wait, irq);

			SystemMetrics systemMetrics = new SystemMetricsImpl(systemId, systemLoadAverage, actualFree,
					actualUsed, swapUsed, openFileDescriptors, swapPageIn, swapPageOut, ioRead, ioWrite,
					userPerc, sysPerc, idlePerc, waitPerc, irqPerc, user, sys, idle, wait, irq,
					processesCount, runningProcessesCount, threadsCount, ns.tcpOutbound, ns.tcpInbound,
					ns.networkSent, ns.networkReceived, ns.loopbackNetworkSent, ns.loopbackNetworkReceived);
			Collector.addMeasurement(systemMetrics);
			logger.info(systemMetrics.toString());
		} catch (SigarException e) {
			logger.error("could not get sigar objects from Sigar library. cause is : " + e.getMessage(), e);
			return; // TODO decide whether stop agent or just this run
		}
	}

	private double getPeriodInSec(long millis) {
		return millis / 1000.0;
	}

	private String formatPeriod(double sec) {
		return new DecimalFormat("#.00").format(sec) + " sec";
	}

	private long getSizeInKb(long bytes) {
		return bytes / 1024;
	}

	private String formatSize(double kbytes) {
		return new DecimalFormat("#").format(kbytes) + " kB";
	}

	private long getOpenFileDescriptors() throws SigarException {
		if (!openFileDescriptorsMetrics) {
			return 0;
		}
		long openFileDescriptors = 0;
		try {
			long[] procList = null;
			procList = sigar.getProcList();
			for (int i = 0; i < procList.length; i++) {
				ProcFd procFd = null;
				try {
					procFd = sigar.getProcFd(procList[i]);
				} catch (SigarPermissionDeniedException e) {
					// logger.error("permission dennied for pid : " +
					// procList[i]);
					continue;
				}
				openFileDescriptors += procFd.getTotal();
			}
		} catch (SigarNotImplementedException e) {
			openFileDescriptorsMetrics = false;
			return 0;
		}
		return openFileDescriptors;
	}

	private class NetworkStatistics {
		long tcpOutbound;
		long tcpInbound;
		long networkSent;
		long networkReceived;
		long loopbackNetworkSent;
		long loopbackNetworkReceived;
	}

	private void refreshNetworkStatistics(NetworkStatistics ns) {
		try {
			NetStat net = sigar.getNetStat();
			ns.tcpOutbound = net.getTcpOutboundTotal();
			ns.tcpInbound = net.getTcpInboundTotal();
			long networkSent = 0;
			long networkReceived = 0;
			long loopbackNetworkSent = 0;
			long loopbackNetworkReceived = 0;
			String[] networkInterfaces = sigar.getNetInterfaceList();
			for (String iface : networkInterfaces) {
				NetInterfaceStat ifaceStat = sigar.getNetInterfaceStat(iface);
				if ("lo".equalsIgnoreCase(iface)) {
					loopbackNetworkSent = ifaceStat.getTxBytes();
					loopbackNetworkReceived = ifaceStat.getRxBytes();
				} else {
					networkSent += ifaceStat.getTxBytes();
					networkReceived += ifaceStat.getRxBytes();
				}
			}
			if (firstRead)
				firstRead = false;
			else {
				ns.networkSent = networkSent - oldNetworkSent;
				ns.networkReceived = networkReceived - oldNetworkReceived;
				ns.loopbackNetworkSent = loopbackNetworkSent - oldLoopbackNetworkSent;
				ns.loopbackNetworkReceived = loopbackNetworkReceived - oldLoopbackNetworkReceived;
			}
			oldNetworkSent = networkSent;
			oldNetworkReceived = networkReceived;
			oldLoopbackNetworkSent = loopbackNetworkSent;
			oldLoopbackNetworkReceived = loopbackNetworkReceived;
		} catch (Exception e) {
			logger.error("Error reading network statistics - log & ignore", e);
		}
	}
}
