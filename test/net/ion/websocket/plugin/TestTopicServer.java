package net.ion.websocket.plugin;

import java.io.File;
import java.net.URI;

import junit.framework.TestCase;
import net.ion.radon.Options;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.config.ServerConfigParser;
import net.ion.websocket.server.ServerNodeRunner;

public class TestTopicServer extends TestCase {

	
	public void testParser() throws Exception {
		ServerConfigParser p = ServerConfigParser.parse(XMLConfig.create(new File("resource/config/server-config.xml"))) ;
		
	}
	
	public void testReadConfig() throws Exception {
		ServerNodeRunner runner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/server-config.xml"}));
		runner.start();
		
		SyncMockClient bleujin = SyncMockClient.newTest() ;
		bleujin.connect(new URI("ws://127.0.0.1:8787/bleujin/1234/")) ;
		
		SyncMockClient hero = SyncMockClient.newTest() ;
		hero.connect(new URI("ws://127.0.0.1:8787/hero/4567/")) ;
		
		
		MessagePacket msgPacket = MessagePacket.create().inner("head").put("receiver", "hero;novision").toRoot().inner("body").put("message", "HelloWorld~ ").toRoot();
		
		bleujin.sendMessage(msgPacket) ;
		
		bleujin.disconnect() ;
		bleujin.await(500) ;
	}
}
