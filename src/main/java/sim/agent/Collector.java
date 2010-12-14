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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.data.Metrics;

/**
 * Thread safe class used to collect all the events/measurements generated by
 * instrumented code and the system measurements generated by agent.
 * 
 * @author vroman
 * 
 */
public class Collector {

	private static final Logger logger = LoggerFactory.getLogger(AgentHandler.class);

	private static ConcurrentLinkedQueue<Metrics> measurements = new ConcurrentLinkedQueue<Metrics>();

	public static void addMeasurement(Metrics metrics) {
		measurements.add(metrics);
	}

	static {
		ServerComunicator ac = new ServerComunicator();
		ac.start();
	}

	private static class ServerComunicator extends Thread {
		private static final String serverLocation = "http://" + Main.serverAddress + ":8099/server";
		private static final long COLLECT_INTERVAL = 5000;
		private static final int TIMEOUT = 5000;
		private static final int MAX_METRICS_WRITE = 1000;

		public ServerComunicator() {
			super("SIM - ServerComunicator");
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				try {
					sleep(COLLECT_INTERVAL);
				} catch (InterruptedException e) {
					break;
				}
				sendMeasurements();
			}
		}

		private void sendMeasurements() {
			ObjectOutputStream serverDataStream = null;
			BufferedReader serverResponseStream = null;
			try {
				URL agentURL = new URL(serverLocation);
				URLConnection serverConnection = agentURL.openConnection();
				serverConnection.setConnectTimeout(TIMEOUT);
				serverConnection.setReadTimeout(TIMEOUT);
				serverConnection.setDoInput(true);
				serverConnection.setDoOutput(true);
				serverConnection.setUseCaches(false);
				serverDataStream = new ObjectOutputStream(serverConnection.getOutputStream());
				int count = 0;
				while (!measurements.isEmpty()) {
					serverDataStream.writeObject(measurements.remove());
					count++;
					if (count >= MAX_METRICS_WRITE)
						break;
				}
				serverDataStream.flush();
				serverDataStream.close();
				serverDataStream = null;
				serverResponseStream = new BufferedReader(new InputStreamReader(
						serverConnection.getInputStream()));
				String agentResponse = serverResponseStream.readLine();
				serverResponseStream.close();
				serverResponseStream = null;
				if (!"SUCCESS".equalsIgnoreCase(agentResponse)) {
					logger.error("SIM-agent: server communication failure1: " + agentResponse);
				}
			} catch (IOException e) {
				logger.error("SIM-agent: server communication failure2: " + e.getMessage());
			} finally {
				if (serverDataStream != null) {
					try {
						serverDataStream.close();
					} catch (IOException e) {
						logger.error("SIM-agent: closing server data data stream error: " + e.getMessage());
					}
				}
				if (serverResponseStream != null) {
					try {
						serverDataStream.close();
					} catch (IOException e) {
						logger.error("SIM-agent: closing server response stream error: " + e.getMessage());
					}
				}
			}
		}
	}
}