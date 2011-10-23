package net.ion.websocket.server;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllServer extends TestCase{
	
	public static TestSuite suite(){
		TestSuite suite = new TestSuite("Test All Server") ;
		
		suite.addTestSuite(TestURIParser.class) ;
		suite.addTestSuite(TestServerConfigParser.class) ;
		suite.addTestSuite(TestContext.class) ;
		
		suite.addTestSuite(TestDefaultServer.class) ;
		suite.addTestSuite(TestServerRunner.class) ;
		suite.addTestSuite(TestEmbedAradon.class) ;
		return suite ;
	}
}
