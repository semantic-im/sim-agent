/**
 * 
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
			mm.getMethodName();
			StringBuilder sb = new StringBuilder();
			sb.append(mm.getMethodName() + "," + mm.getClassName() + "," + mm.getException());
			logger.info(sb.toString());
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
