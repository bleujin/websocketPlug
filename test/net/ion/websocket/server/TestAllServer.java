package net.ion.websocket.server;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllServer extends TestCase{
	
	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		
		suite.addTestSuite(TestServerConfigParser.class) ;
		
		suite.addTestSuite(TestServerRunner.class) ;
//		suite.addTestSuite(TestDeploy.class) ;
		suite.addTestSuite(TestURIParser.class) ;
		suite.addTestSuite(TestContext.class) ;
		return suite ;
	}
}
