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
		
		
		server.stopServer() ;
		Debug.debug(c.getLastPacket()) ;
	}
	

	public void testServerRecive() throws Exception {
		server.getPlugInChain().addPlugIn(new EchoPlugIn());
		server.startServer() ;

		SyncMockClient mock = SyncMockClient.newTest();
		mock.connect(uri);
		mock.sendMessage(MessagePacket.create().inner(BODY).put("greeting", "Hi").toRoot());
		mock.await(300) ;
		mock.disconnect();
		
		MessagePacket received = mock.getLastPacket() ;
		assertEquals("Hi", received.get("body/greeting"));
	}
}
