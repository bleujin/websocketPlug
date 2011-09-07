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
		
		Thread.sleep(2000) ;
		Debug.debug(c.getLastPacket().getFullString()) ;
		c.sendMessage(MessagePacket.PING) ;
		c.sendMessage(MessagePacket.PING) ;
		
		
		server.stopServer() ;
		Debug.debug(c.getLastPacket()) ;
	}
}
