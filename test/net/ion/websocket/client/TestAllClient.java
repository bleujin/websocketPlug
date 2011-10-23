package net.ion.websocket.client;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllClient extends TestCase{

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Test All Client");
		suite.addTestSuite(TestSyncMockClient.class) ;
		suite.addTestSuite(TestMessagePacket.class) ;
		
		suite.addTestSuite(TestClientListener.class) ;

		return suite;
	}
}
