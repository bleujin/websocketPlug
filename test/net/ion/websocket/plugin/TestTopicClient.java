package net.ion.websocket.plugin;

import java.net.URI;

import junit.framework.TestCase;
import net.ion.framework.util.StringUtil;
import net.ion.websocket.client.SyncMockClient;

public class TestTopicClient extends TestCase {
	
	public void testCall() throws Exception {
		SyncMockClient bleujin = SyncMockClient.newTest() ;
		bleujin.connect( new URI("ws://127.0.0.1:8787/bleujin/1234")) ;
		
		SyncMockClient hero = SyncMockClient.newTest() ;
		hero.connect( new URI("ws://127.0.0.1:8787/hero/1234")) ;
		
		MessagePacket packet = MessagePacket.create().inner("head").put("sender", "bleujin").put("receiver", "bleujin;novision;hero") .toRoot() ;
		bleujin.sendMessage(packet) ;
		hero.awaitOnMessage() ;
		
		assertEquals("bleujin", hero.getLastPacket().get("head/sender")) ;
		assertEquals(true, StringUtil.isNotBlank(hero.getLastPacket().getString("head/msgid"))) ;
		assertEquals(true, StringUtil.isNotBlank(hero.getLastPacket().getString("head/created"))) ;
		
		bleujin.disconnect() ;
		hero.disconnect() ;
	}
}
