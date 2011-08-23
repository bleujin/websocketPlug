package net.ion.websocket.plugin;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.websocket.common.plugin.TestChannelPlugIn;

public class TestAllPlugIn  extends TestCase{

	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		
		suite.addTestSuite(TestPlugIn.class) ;
		
		suite.addTestSuite(TestChannelPlugIn.class) ;
		
		return suite ;
	}
}
