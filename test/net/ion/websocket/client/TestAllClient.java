package net.ion.websocket.client;

import net.ion.websocket.plugin.TestMessagePacket;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllClient extends TestCase{

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestSyncMockClient.class) ;
		suite.addTestSuite(TestMessagePacket.class) ;
		

		return suite;
	}
}
