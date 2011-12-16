package net.ion.websocket.common.plugin;

import net.ion.framework.util.Debug;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.plugin.MessagePacket;

public class TestEchoPlugIn extends TestBaseWebSocket{

	public void testConnected() throws Exception {
		server.getPlugInChain().addPlugIn(new EchoPlugIn()) ;
		server.startServer() ;
		

		SyncMockClient c = SyncMockClient.newTest() ;
		connectAsUser(c, "bleujin");
		
		c.await(500) ;
		Debug.debug(c.getLastPacket().getFullString()) ;
		c.sendMessage(MessagePacket.PING) ;
		c.sendMessage(MessagePacket.PING) ;
		
		
		synchronized (this) {
			wait(100) ;
		}
		c.disconnect() ;
		synchronized (this) {
			wait(100) ;
		}
		
		server.stopServer() ;
		Debug.debug(c.getLastPacket()) ;
	}
	

	public void testServerRecive() throws Exception {
		server.getPlugInChain().addPlugIn(new EchoPlugIn());
		server.startServer() ;

		SyncMockClient c = SyncMockClient.newTest();
		c.connect(uri);
		c.sendMessage(MessagePacket.create().inner(BODY).put("greeting", "Hi").toRoot());
		c.await(300) ;
		c.disconnect();
		
		MessagePacket received = c.getLastPacket() ;
		assertEquals("Hi", received.get("body/greeting"));
	}
}
