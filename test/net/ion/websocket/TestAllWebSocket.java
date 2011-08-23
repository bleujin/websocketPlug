package net.ion.websocket;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.websocket.client.TestAllClient;
import net.ion.websocket.common.listener.TestAllListener;
import net.ion.websocket.plugin.TestAllPlugIn;
import net.ion.websocket.server.TestAllServer;

public class TestAllWebSocket extends TestCase {

	
	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		
		suite.addTest(TestAllClient.suite()) ;
		
		suite.addTest(TestAllServer.suite()) ;
		
		
		suite.addTest(TestAllPlugIn.suite()) ;
		suite.addTest(TestAllListener.suite()) ;
		
		return suite ;
	}
}
