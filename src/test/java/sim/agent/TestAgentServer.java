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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;
import sim.data.ApplicationId;
import sim.data.Method;
import sim.data.MethodImpl;
import sim.data.MethodMetricsImpl;
import sim.data.SystemId;

/**
 * 
 */

/**
 * @author valer
 *
 */
public class TestAgentServer extends TestCase {

	private ApplicationId applicationId;
	private SystemId systemId;

	@Override
	protected void setUp() throws Exception {
		AgentServerThread agentServerThread = new AgentServerThread(systemId);
		Thread thread = new Thread(agentServerThread);
		thread.run();
	}

	public TestAgentServer() {
		this.applicationId = new ApplicationId("1", "App 1");
		this.systemId = new SystemId("1", "System 1", 1024 * 1024 * 1024, 2);
	}

	public void test() throws MalformedURLException, IOException {
		URL url = new URL("http://localhost:8088/agent");
		URLConnection urlConn = url.openConnection();
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);

		//DataOutputStream dos = new DataOutputStream (urlConn.getOutputStream());

		Method method = new MethodImpl(applicationId, TestAgentServer.class.getName(), "test");

		MethodMetricsImpl tmm = new MethodMetricsImpl(method);
		ObjectOutputStream oos = new ObjectOutputStream(urlConn.getOutputStream());
		oos.writeObject(tmm);

		oos.writeObject(tmm);

		//String message = "NEW_ITEM=" + URLEncoder.encode("sdfgsdsgs sdfsf", "UTF-8");
		//dos.writeBytes(message);
		oos.flush();
		oos.close();

		// the server responds by saying
		// "SUCCESS" or "FAILURE"

		BufferedReader dis = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		String s = dis.readLine();
		dis.close();

		assertEquals(s, "SUCCESS");
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		TestAgentServer testAgent = new TestAgentServer();
		testAgent.test();
	}

}
