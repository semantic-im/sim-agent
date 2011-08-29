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

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.data.SystemId;

/**
 * This class is responsible to start up the SIM-Agent application.
 * 
 * SIM-Agent acts as a measurements collector for all local instrumented
 * applications, it also collects system wide measurements and sends all
 * collected measurements to the server.
 * 
 * @author mcq
 * 
 */
public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private static final String JAVA_LIBRARY_PATH = "java.library.path";

	private static final String MS = "MS";
	private static final String S = "S";
	private static final String M = "M";

	private int initialDelay = 0;
	private int period = 5;
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	public static String serverAddress = "localhost";

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/*
	 * Starts the Http Server listening for method metrics
	 */
	public void startServer(SystemId systemId) {
		AgentServerThread agentServerThread = new AgentServerThread(systemId);
		Thread thread = new Thread(agentServerThread);
		thread.run();
	}

	/*
	 * Runs the agent thread and also start the http server
	 */
	public boolean runAgent() {
		assert period > 0;

		// generate id and get name
		SystemId systemId = generateSystemId();

		startServer(systemId);

		AgentThread agentThread = new AgentThread(systemId);
		try {
			scheduler.scheduleAtFixedRate(agentThread, initialDelay, period, timeUnit);
		} catch (RejectedExecutionException e) {
			logger.error("could not start agent process, cause is : " + e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		Main main = new Main();

		URL libURL = main.getClass().getResource("/logback.xml");
		String libraryPath = System.getProperties().getProperty(JAVA_LIBRARY_PATH);
		libraryPath += ":.:" + libURL.getPath().substring(0, libURL.getPath().lastIndexOf("/"));
		System.getProperties().setProperty(JAVA_LIBRARY_PATH, libraryPath);

		switch (args.length) {
		case 0:
			main.printUsage();
			break;
		case 4:
			Main.serverAddress = args[3];
		case 3:
			main.initialDelay = main.readInitialDelay(args[2]);
		case 2:
			main.timeUnit = main.readTimeUnit(args[1]);
		case 1:
			main.period = main.readPeriod(args[0]);
		}
		System.out.println("using parameters : period=" + main.period + ", timeUnit=" + main.timeUnit
				+ ", initialDelay=" + main.initialDelay + ", server=" + Main.serverAddress);

		System.out.println("Starting Metrics Agent ...");
		if (main.runAgent()) {
			System.out.println("Metrics Agent successfully started.");
		} else {
			System.out.println("An error occured starting Metrics Agent. Check logs for errors.");
		}
	}

	private void printUsage() {
		System.out.println("Metrics Agent Usage :");
		System.out.println("");
		System.out.println("\tjava sim.agent.Main period timeUnit initialDelay server");
		System.out.println("");
		System.out.println("\tperiod : the time period between each metrics read; 5 is the default value.");
		System.out
		.println("\ttimeUnit : the time unit of the period and initial delay parameters. Possible values are MS, S (default value) or M (milliseconds, seconds or minutes)");
		System.out.println("\tinitialDelay : the time to delay first execution; 0 is default");
		System.out.println("\tserver : server address; localhost is default");
		System.out.println("");
	}

	private int readInitialDelay(String initialDelayString) {
		int anInitialDelay = 0;
		try {
			anInitialDelay = new Integer(initialDelayString);
		} catch (NumberFormatException e) {
			System.out.println("The initialDelay parameter must be an integer greater or equal than 0");
			System.exit(1);
		}
		return anInitialDelay;
	}

	private int readPeriod(String periodString) {
		int anPeriod = 0;
		try {
			anPeriod = new Integer(periodString);
		} catch (NumberFormatException e) {
			System.out.println("The period parameter must be an integer greater than 0");
			System.exit(1);
		}
		return anPeriod;
	}

	private TimeUnit readTimeUnit(String timeUnitString) {
		TimeUnit anTimeUnit = TimeUnit.SECONDS;
		timeUnitString = timeUnitString == null ? "" : timeUnitString;
		if (timeUnitString.toUpperCase().equals(MS)) {
			anTimeUnit = TimeUnit.MILLISECONDS;
		} else if (timeUnitString.toUpperCase().equals(S)) {
			anTimeUnit = TimeUnit.SECONDS;
		} else if (timeUnitString.toUpperCase().equals(M)) {
			anTimeUnit = TimeUnit.MINUTES;
		} else {
			System.out.println("The time unit parameter can have one of the values : MS, S and M. "
					+ "Those stand for milliseconds, seconds and minutes.");
			System.exit(1);
		}
		return anTimeUnit;
	}

	private static SystemId generateSystemId() {
		String id = UUID.randomUUID().toString();
		return new SystemId(id, getSystemName());
	}

	private static String getSystemName() {
		StringBuilder name = new StringBuilder();
		name.append(System.getProperty("os.name")).append(" ");
		name.append(System.getProperty("os.arch")).append(" ");
		name.append(System.getProperty("os.version")).append(" ");
		name.append(getHostName()).append(" ");
		name.append(getMAC());
		return name.toString().trim();
	}

	private static String getMAC() {
		StringBuilder mac = new StringBuilder();
		try {
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e
					.hasMoreElements();) {
				NetworkInterface ni = e.nextElement();
				if (ni.isLoopback() || ni.isPointToPoint())
					continue;
				byte[] ha = ni.getHardwareAddress();
				if (ha != null) {
					for (int i = 0; i < ha.length; i++) {
						mac.append(Integer.toHexString(ha[i]));
					}
					break;
				}
			}
		} catch (SocketException e) {}
		return mac.toString();
	}

	private static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "";
		}
	}
}
