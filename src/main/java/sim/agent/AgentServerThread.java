/**
 * 
 */
package sim.agent;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

/**
 * @author valer
 *
 */
public class AgentServerThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AgentServerThread.class);
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {		
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8088), 0);
			server.createContext("/agent", new AgentHandler());
			server.start();
		} catch (IOException e) {
			logger.error("failed to start agent server", e);
		}
	}

}
