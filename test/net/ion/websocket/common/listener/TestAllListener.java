package net.ion.websocket.common.listener;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllListener extends TestCase{
	
	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		suite.addTestSuite(TestListener.class) ;
		suite.addTestSuite(TestChannelListener.class) ;
		
		return suite ;
	}

}
