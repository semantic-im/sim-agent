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
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.data.SystemId;

import com.sun.net.httpserver.HttpServer;

/**
 * Thread running the Http Server listening for method metrics
 * 
 * @author valer
 *
 */
@SuppressWarnings("restriction")
public class AgentServerThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AgentServerThread.class);
	
	private SystemId systemId;
	
	public AgentServerThread(SystemId systemId) {
		this.systemId = systemId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {		
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8088), 0);
			server.createContext("/agent", new AgentHandler(systemId));
			server.start();
		} catch (IOException e) {
			logger.error("failed to start agent server", e);
		}
	}

}
