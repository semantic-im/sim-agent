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
import java.io.ObjectInputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.data.MethodMetrics;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * The http handle reading method metrics objects.
 * 
 * @author valer
 *
 */
public class AgentHandler implements HttpHandler {

	private static final Logger logger = LoggerFactory.getLogger(AgentHandler.class);
	
	@Override
	public void handle(HttpExchange xchg) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(xchg.getRequestBody());
		Object o = null;
		try {
			o = ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (o instanceof MethodMetrics) {
			MethodMetrics mm = (MethodMetrics) o;
			logger.info(mm.toString());
		}

		xchg.sendResponseHeaders(200, "SUCCESS".length());
	    OutputStream os = xchg.getResponseBody();
	    os.write("SUCCESS".getBytes());
	    os.close();

		/*
	    Headers headers = xchg.getRequestHeaders();
	    Set<Map.Entry<String, List<String>>> entries = headers.entrySet();

	    StringBuffer response = new StringBuffer();
	    for (Map.Entry<String, List<String>> entry : entries)
	      response.append(entry.toString() + "\n");

	    xchg.sendResponseHeaders(200, response.length());
	    OutputStream os = xchg.getResponseBody();
	    os.write(response.toString().getBytes());
	    os.close();
	    */
	}

}
