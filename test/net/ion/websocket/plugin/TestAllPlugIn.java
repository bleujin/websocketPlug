package net.ion.websocket.plugin;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.websocket.common.plugin.TestBroadCastPlugIn;
import net.ion.websocket.common.plugin.TestChannelPlugIn;
import net.ion.websocket.common.plugin.TestEchoPlugIn;
import net.ion.websocket.common.plugin.TestPlugIn;

	public class TestAllPlugIn  extends TestCase{
	
		public static TestSuite suite(){
			TestSuite suite = new TestSuite("Test All PlugIn") ;
			
			suite.addTestSuite(TestPlugIn.class) ;
			suite.addTestSuite(TestEchoPlugIn.class) ;
			suite.addTestSuite(TestBroadCastPlugIn.class) ;
			suite.addTestSuite(TestChannelPlugIn.class) ;
			
			return suite ;
		}
	}
