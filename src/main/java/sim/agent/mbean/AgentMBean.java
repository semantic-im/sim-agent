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
package sim.agent.mbean;

/**
 * @author valer
 *
 */
public interface AgentMBean {

	public double getSystemLoadAverage();

	public long getTotalSystemFreeMemory();
	
	public long getTotalSystemUsedMemory();
	
	public long getTotalSystemUsedSwap();
	
	public long getSystemOpenFileDescriptors();
	
	public long getSwapIn();
	
	public long getSwapOut();

	public long getIORead();
	
	public long getIOWrite();

	public double getUserPerc();
	
	public double getSysPerc();
	
	public double getIdlePerc();
	
	public double getWaitPerc();
	
	public double getIrqPerc();

	public double getUser();
	
	public double getSys();
	
	public double getIdle();
	
	public double getWait();
	
	public double getIrq();

	public void loadData(double systemLoadAverage, long totalSystemFreeMemory, long totalSystemUsedMemory, long totalSystemUsedSwap,
			long systemOpenFileDescriptors, long swapIn, long swapOut, long ioRead, long ioWrite, double userPerc, double sysPerc, 
			double idlePerc, double waitPerc, double irqPerc, double user, double sys, double idle, double wait, double irq);
	
}
