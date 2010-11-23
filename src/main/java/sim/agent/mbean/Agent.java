/**
 * 
 */
package sim.agent.mbean;

/**
 * @author vroman
 *
 */
public class Agent implements AgentMBean {

	private double systemLoadAverage;
	private long totalSystemFreeMemory;
	private long totalSystemUsedMemory;
	private long totalSystemUsedSwap;
	private long systemOpenFileDescriptors;
	private long swapIn;
	private long swapOut;
	private long ioRead;
	private long ioWrite;
	private double userPerc;
	private double sysPerc;
	private double idlePerc;
	private double waitPerc;
	private double irqPerc;
	private double user;
	private double sys;
	private double idle;
	private double wait;
	private double irq;
	
	@Override
	public void loadData(double systemLoadAverage, long totalSystemFreeMemory,
			long totalSystemUsedMemory, long totalSystemUsedSwap,
			long systemOpenFileDescriptors, long swapIn, long swapOut,
			long ioRead, long ioWrite, double userPerc, double sysPerc,
			double idlePerc, double waitPerc, double irqPerc, double user,
			double sys, double idle, double wait, double irq) {
		this.systemLoadAverage = systemLoadAverage;
		this.totalSystemFreeMemory = totalSystemFreeMemory;
		this.totalSystemUsedMemory = totalSystemUsedMemory;
		this.totalSystemUsedSwap = totalSystemUsedSwap;
		this.systemOpenFileDescriptors = systemOpenFileDescriptors;
		this.swapIn = swapIn;
		this.swapOut = swapOut;
		this.ioRead = ioRead;
		this.ioWrite = ioWrite;
		this.userPerc = userPerc;
		this.sysPerc = sysPerc;
		this.idlePerc = idlePerc;
		this.waitPerc = waitPerc;
		this.irqPerc = irqPerc;
		this.user = user;
		this.sys = sys;
		this.idle = idle;
		this.wait = wait;
		this.irq = irq;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getTotalSystemFreeMemory()
	 */
	@Override
	public long getTotalSystemFreeMemory() {
		return totalSystemFreeMemory;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getTotalSystemUsedMemory()
	 */
	@Override
	public long getTotalSystemUsedMemory() {
		return totalSystemUsedMemory;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getTotalSystemUsedSwap()
	 */
	@Override
	public long getTotalSystemUsedSwap() {
		return totalSystemUsedSwap;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getSystemOpenFileDescriptors()
	 */
	@Override
	public long getSystemOpenFileDescriptors() {
		return systemOpenFileDescriptors;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getSwapIn()
	 */
	@Override
	public long getSwapIn() {
		return swapIn;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getSwapOut()
	 */
	@Override
	public long getSwapOut() {
		return swapOut;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getIORead()
	 */
	@Override
	public long getIORead() {
		return ioRead;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getIOWrite()
	 */
	@Override
	public long getIOWrite() {
		return ioWrite;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getUserPerc()
	 */
	@Override
	public double getUserPerc() {
		return userPerc;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getSysPerc()
	 */
	@Override
	public double getSysPerc() {
		return sysPerc;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getIdlePerc()
	 */
	@Override
	public double getIdlePerc() {
		return idlePerc;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getWaitPerc()
	 */
	@Override
	public double getWaitPerc() {
		return waitPerc;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getIrqPerc()
	 */
	@Override
	public double getIrqPerc() {
		return irqPerc;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getUser()
	 */
	@Override
	public double getUser() {
		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getSys()
	 */
	@Override
	public double getSys() {
		return sys;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getIdle()
	 */
	@Override
	public double getIdle() {
		return idle;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getWait()
	 */
	@Override
	public double getWait() {
		return wait;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getIrq()
	 */
	@Override
	public double getIrq() {
		return irq;
	}

	/* (non-Javadoc)
	 * @see sim.agent.mbean.AgentMBean#getSystemLoadAverage()
	 */
	@Override
	public double getSystemLoadAverage() {
		return systemLoadAverage;
	}

}
