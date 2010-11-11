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
package sim.data.test;
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

	@Override
	public String toString() {
		return this.getMethodName() + "," + this.getClassName() + "," + this.getException() + "," + this.endedWithError() + "," + getBeginExecutionTime() +
			"," + this.getEndExecutionTime() + "," + getWallClockTime() + "," + this.getThreadUserCpuTime() + "," + this.getThreadSystemCpuTime() +
			"," + this.getThreadTotalCpuTime() + "," + this.getThreadCount() + "," + this.getThreadBlockCount() + "," + this.getThreadBlockTime() +
			"," + this.getThreadWaitCount() + "," + this.getThreadWaitTime() + "," + this.getThreadGccCount() + "," + this.getThreadGccTime() +
			"," + this.getProcessTotalCpuTime();
	}

}
