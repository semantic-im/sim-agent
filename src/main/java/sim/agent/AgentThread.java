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
import org.hyperic.sigar.ProcFd;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;
import org.hyperic.sigar.SigarPermissionDeniedException;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.agent.mbean.Agent;
import sim.data.Collector;
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
	
	boolean openFileDescriptorsMetrics = true;
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//logger.info("agent running ... ");

		Mem mem = null;
		Swap swap = null;
		
		long readBytes = 0;
		long writeBytes = 0;
		CpuPerc cpuPerc = null;
		Cpu cpu = null;
		try {
			mem = sigar.getMem();
			swap = sigar.getSwap();
			
			FileSystem[] fileSystemList = sigar.getFileSystemList();
			for (int i = 0; i < fileSystemList.length; i++) {
				String devName = fileSystemList[i].getDevName();
				//logger.info("get file system usage for filesystem : " + devName);
				FileSystemUsage fileSystemUsage = null;
				try {
					fileSystemUsage = sigar.getFileSystemUsage(devName);
				} catch (SigarException e) {
					//logger.warn("could not get file system usage for device name : " + devName);
					continue;
				}
				readBytes += fileSystemUsage.getDiskReadBytes();
				writeBytes += fileSystemUsage.getDiskWriteBytes();
			}
			
			cpuPerc = sigar.getCpuPerc();
			cpu = sigar.getCpu(); //take the whole list of cpus
		
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
			
			agentMBean.loadData(systemLoadAverage, actualFree, actualUsed, swapUsed, openFileDescriptors, swapPageIn, swapPageOut,
					ioRead, ioWrite, userPerc, sysPerc, idlePerc, waitPerc, irqPerc, user, sys, idle, wait, irq);
			
			SystemMetrics systemMetrics = new SystemMetricsImpl(systemId, systemLoadAverage, actualFree, actualUsed, swapUsed, openFileDescriptors, swapPageIn, swapPageOut,
					ioRead, ioWrite, userPerc, sysPerc, idlePerc, waitPerc, irqPerc, user, sys, idle, wait, irq);
			Collector.addMeasurement(systemMetrics);
			
			logger.info("system load average : " + CpuPerc.format(systemLoadAverage));
			logger.info("total system free memory : " + formatSize(actualFree));
			logger.info("total system used memory : " + formatSize(actualUsed));
			logger.info("total system used swap space : " + formatSize(swapUsed));
			logger.info("system open file descriptor count : " + openFileDescriptors);
			logger.info("swap in : " + swapPageIn + " pages");
			logger.info("swap out : " + swapPageOut + " pages");
			logger.info("i/o in : " + formatSize(ioRead));
			logger.info("i/o out : " + formatSize(ioWrite));
			//logger.info("system context switches : " + cpuPerc.);
			logger.info("user % : " + CpuPerc.format(userPerc));
			logger.info("system % : " + CpuPerc.format(sysPerc));
			logger.info("idle % : " + CpuPerc.format(idlePerc));
			logger.info("wait % : " + CpuPerc.format(waitPerc));
			logger.info("irq % : " + CpuPerc.format(irqPerc));
			logger.info("user time : " + formatPeriod(user));
			logger.info("system time : " + formatPeriod(sys));
			logger.info("idle time : " + formatPeriod(idle));
			logger.info("wait time : " + formatPeriod(wait));
			logger.info("irq time : " + formatPeriod(irq));
		} catch (SigarException e) {
			logger.error("could not get sigar objects from Sigar library. cause is : " + e.getMessage(), e);
			return; //TODO decide whether stop agent or just this run
		}
	}

	private double getPeriodInSec(long millis) {
		return millis/1000.0;
	}

	private String formatPeriod(double sec) {
		return new DecimalFormat("#.00").format(sec) + " sec";
	}

	private long getSizeInKb(long bytes) {
		return bytes/1024;
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
			for (int i =0; i < procList.length; i++) {
				ProcFd procFd = null;
				try {
					procFd = sigar.getProcFd(procList[i]);
				} catch (SigarPermissionDeniedException e) {
					//logger.error("permission dennied for pid : " + procList[i]);
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
}
