package net.ion.websocket.client;

import junit.framework.TestCase;
import junit.framework.TestSuite;


public class TestClient extends TestCase {
	
	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		
		suite.addTestSuite(TestSyncMockClient.class) ;
		
		return suite ;
	}

}
