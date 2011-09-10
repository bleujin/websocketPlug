package net.ion.websocket.common.plugin;

import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.plugin.AllBroadCastPlugIn;
import net.ion.websocket.plugin.MessagePacket;

public class TestBroadCastPlugIn extends TestBaseWebSocket {

	public void testBroadCast() throws Exception {
		server.getPlugInChain().addPlugIn(new AllBroadCastPlugIn());
		server.startServer() ;
		
		assertEquals(1, server.getPlugInChain().getPlugIns().size()) ;
		
		SyncMockClient bleu = SyncMockClient.newTest();
		super.connectAsUser(bleu, "bleu") ;

		SyncMockClient hero = SyncMockClient.newTest();
		super.connectAsUser(hero, "hero") ;

		bleu.sendMessage(MessagePacket.create().inner(BODY).put("greeting", "Hi").toRoot());
		hero.awaitOnMessage() ;
//		bleu.awaitOnMessage() ;
		
		assertEquals("Hi", hero.getLastPacket().getString("body.greeting"));
		
		bleu.disconnect();
		hero.disconnect();
	}
	
}
