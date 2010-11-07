package sim.data;
import java.util.HashMap;
import java.util.Map;

import sim.data.MethodMetrics;

public class MethodMetricsImpl implements MethodMetrics {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4927399823762011575L;

	@Override
	public String getMethodName() {
		return "testMethod";
	}

	@Override
	public String getClassName() {
		return "TestMethodMetrics";
	}

	@Override
	public Map<String, Object> getContext() {
		return new HashMap<String, Object>();
	}

	@Override
	public String getException() {
		return "exception asdasadas";
	}

	@Override
	public boolean endedWithError() {
		return false;
	}

	@Override
	public long getBeginExecutionTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getEndExecutionTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getWallClockTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getThreadUserCpuTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getThreadSystemCpuTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getThreadTotalCpuTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getThreadCount() {
		return 23;
	}

	@Override
	public long getThreadBlockCount() {
		return 12;
	}

	@Override
	public long getThreadBlockTime() {
		return 134440;
	}

	@Override
	public long getThreadWaitCount() {
		return 2;
	}

	@Override
	public long getThreadWaitTime() {
		return 34345;
	}

	@Override
	public long getThreadGccCount() {
		return 0;
	}

	@Override
	public long getThreadGccTime() {
		return 0;
	}

	@Override
	public long getProcessTotalCpuTime() {
		return 0;
	}

}
