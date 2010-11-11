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

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcFd;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarPermissionDeniedException;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the thread reading the system metrics. It is running periodically.
 * 
 * @author valer
 *
 */
public class AgentThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AgentThread.class);
	
	private Sigar sigar = null;
	
	/*
	 * Initializae the agent thread
	 */
	public AgentThread() {
		sigar = new Sigar();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//logger.info("agent running ... ");

		long openFileDescriptors = 0;
		
		Mem mem = null;
		Swap swap = null;
		long[] procList = null;
		long readBytes = 0;
		long writeBytes = 0;
		CpuPerc cpuPerc = null;
		Cpu cpu = null;
		try {
			mem = sigar.getMem();
			swap = sigar.getSwap();
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
			
			FileSystem[] fileSystemList = sigar.getFileSystemList();
			for (int i = 0; i < fileSystemList.length; i++) {
				FileSystemUsage fileSystemUsage = sigar.getFileSystemUsage(fileSystemList[i].getDirName());
				readBytes += fileSystemUsage.getDiskReadBytes();
				writeBytes += fileSystemUsage.getDiskWriteBytes();
			}
			
			cpuPerc = sigar.getCpuPerc();
			cpu = sigar.getCpu(); //take the whole lis tof cpus
		} catch (SigarException e) {
			logger.error("could not get sigar objects from Sigar library. cause is : " + e.getMessage(), e);
			return; //TODO decide wether stop agent or just this run
		}
		
		OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

		logger.info("system load average : " + osMXBean.getSystemLoadAverage());
		logger.info("total system free memory : " + mem.getActualFree());
		logger.info("total system used memory : " + mem.getActualUsed());
		logger.info("total system used swap space : " + swap.getUsed());
		logger.info("system open file descriptor count : " + openFileDescriptors);
		logger.info("swap in : " + swap.getPageIn());
		logger.info("swap out : " + swap.getPageOut());
		logger.info("i/o in : " + readBytes);
		logger.info("i/o out : " + writeBytes);
		logger.info("system intrerrupts percentage : " + cpuPerc.getIrq());
		//logger.info("system context switches : " + cpuPerc.);
		logger.info("user time : " + cpu.getUser());
		logger.info("system time : " + cpu.getSys());
		logger.info("idle time : " + cpu.getIdle());
		logger.info("wait time : " + cpu.getWait());
	}

}
