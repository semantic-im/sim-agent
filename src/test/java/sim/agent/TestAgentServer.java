package sim.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import sim.data.MethodMetricsImpl;

import junit.framework.TestCase;

/**
 * 
 */

/**
 * @author valer
 *
 */
public class TestAgentServer extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		AgentServerThread agentServerThread = new AgentServerThread();
		Thread thread = new Thread(agentServerThread);
		thread.run();
	}

	public TestAgentServer() {
		
	}
	
	public void test() throws MalformedURLException, IOException {
		URL url = new URL("http://localhost:8088/agent"); 
	    URLConnection urlConn = url.openConnection(); 
	    urlConn.setDoInput(true); 
	    urlConn.setDoOutput(true); 
	    urlConn.setUseCaches(false);
	    
	    //DataOutputStream dos = new DataOutputStream (urlConn.getOutputStream());
	    	    
	    MethodMetricsImpl tmm = new MethodMetricsImpl();
	    ObjectOutputStream oos = new ObjectOutputStream(urlConn.getOutputStream());
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
