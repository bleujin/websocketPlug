package net.ion.aradon.websocket;

import junit.framework.TestCase;
import net.ion.websocket.common.plugin.DebugPlugIn;
import net.ion.websocket.common.plugin.EchoPlugin;
import net.ion.websocket.server.DefaultServer;
import net.ion.websocket.server.engine.netty.NettyEngine;

public class TestClientsWrapper extends TestCase{

	
	public void testRun() throws Exception {
		DefaultServer ds = runServer() ;
		
		ClientWrapper cw = new ClientWrapper() ;
		cw.runClient("mock", "127.0.0.1", "9000") ;
		
		assertEquals(1, cw.getServerList().size()) ;
		
		Thread.sleep(100) ;
		assertEquals(1, ds.getAllConnectors().length) ;
	}
	
	
	
	private DefaultServer runServer() throws Exception {
		DefaultServer server = new DefaultServer(NettyEngine.test());
		server.getPlugInChain().addPlugIn(new EchoPlugin()) ;
		server.getPlugInChain().addPlugIn(new DebugPlugIn()) ;
		
		server.startServer() ;
		return server ;
	}
}
