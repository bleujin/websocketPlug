package net.ion.websocket.server;

import java.net.URI;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.Options;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.plugin.MessagePacket;

import org.restlet.representation.Representation;

public class TestDeploy extends TestCase{

	public void testReadConfig1() throws Exception {
		Aradon aradon = new Aradon() ;
		aradon.init("resource/config/front-aradon-config.xml") ;
		aradon.startServer(8080) ;
		
		ServerNodeRunner runner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/server-config.xml"}));
		runner.start();

		
		Representation re = AradonClientFactory.create("http://127.0.0.1:8080").createRequest("/session/login/" + RandomUtil.nextRandomString(10), "kalce1", "79b89fc21c4e9b2beac6d2ad03ec2cc9").get() ;
		String path = MessagePacket.load(re.getText()).getString("path") ;
		
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(new URI(path)) ;
		
		// client.sendMessage(MessagePacket.PING) ;
		client.sendMessage(MessagePacket.create().inner("head").put("receiver", "kalce1;kalce2").toRoot().inner("body").put("greeting", "hi")) ;
		client.awaitOnMessage();
		
		
		client.disconnect() ;
		
		Thread.sleep(2000) ;
		runner.stop() ;
		assertEquals(true, runner.getAradon().isStopped()) ;
		
		aradon.stop() ;
	}
	
	
	public void testReadConfig2() throws Exception {
		Aradon aradon = new Aradon() ;
		aradon.init("resource/config/front-aradon-config.xml") ;
		aradon.startServer(8080) ;
		
		ServerNodeRunner runner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/server-config.xml"}));
		runner.start();

		
		Debug.debug(aradon.getChildService("session").getChildren()) ;
		
		Representation re = AradonClientFactory.create(aradon).createRequest("/session/login/" + RandomUtil.nextRandomString(10), "kalce1", "79b89fc21c4e9b2beac6d2ad03ec2cc9").get() ;
		String path = MessagePacket.load(re.getText()).getString("path") ;
		
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(new URI(path)) ;
		
		// client.sendMessage(MessagePacket.PING) ;
		client.sendMessage(MessagePacket.create().inner("head").put("receiver", "kalce1;kalce2").toRoot().inner("body").put("greeting", "hi")) ;
		client.awaitOnMessage();
		
		
		client.disconnect() ;
		
		Thread.sleep(2000) ;
		runner.stop() ;
		assertEquals(true, runner.getAradon().isStopped()) ;
		
		aradon.stop() ;
	}
	
	

}
