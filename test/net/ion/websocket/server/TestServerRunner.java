package net.ion.websocket.server;

import java.net.URI;
import java.util.List;

import javolution.util.FastList;
import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.Options;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.api.Selector;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.plugin.EchoPlugIn;
import net.ion.websocket.common.plugin.flashbridge.FlashBridgePlugIn;
import net.ion.websocket.plugin.MessagePacket;
import net.ion.websocket.server.context.ServiceContext;
import net.ion.websocket.server.engine.EngineConfig;
import net.ion.websocket.server.engine.netty.NettyEngine;
import net.ion.websocket.server.engine.tcp.TCPEngine;

import org.apache.commons.configuration.ConfigurationException;
import org.restlet.representation.Representation;

public class TestServerRunner extends TestCase {

	public void xtestNotFoundConfigFile() throws Exception {
		ServerNodeRunner runner = null ;
		try {
			runner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/server-config"}));
			runner.start();
			fail();
		} catch (ConfigurationException ignore) {
		} finally {
			if (runner != null) runner.stop() ;
		}
	}

	
	
	public void xtestStart() throws Exception {
		Options option = new Options(new String[]{"-config:resource/config/server-config.xml"}) ;
		ServerNodeRunner runner = new ServerNodeRunner(option);
	
		runner.start();

		assertEquals("/{userId}/{sessionId}/{params}", runner.getServer().getConfiguration().getURIPath());
		new InfinityThread().startNJoin() ;
	}

	
	public void testConnectUriParamter() throws Exception {
		WebSocketServer server = new DefaultServer(TestBaseWebSocket.TEST_CONFIG, new NettyEngine(EngineConfig.test(9000)), ServiceContext.createRoot()) ;
		server.startServer() ;
		
		assertEquals("/{userId}/{sessionId}/{params}", server.getConfiguration().getURIPath());

		SyncMockClient client = SyncMockClient.newTest();
		client.connect(new URI("ws://61.250.201.157:9000/bleujin/12312122/timeout=3600000"));
		synchronized (this) {
			wait(200) ;
		}
		client.sendMessage(MessagePacket.PING) ;
		
		Thread.sleep(500) ;
		WebSocketConnector[] conns = server.getAllConnectors() ;
		assertEquals(1, conns.length) ;
		assertEquals("bleujin", conns[0].getString("userId")) ;
		
		WebSocketConnector find = server.findConnector(new Selector(){
			public boolean isTrueCondition(WebSocketConnector conn) {
				return "bleujin".equals(conn.getString("userId"));
			}}) ;
		
		assertTrue(find != null) ;
		assertEquals("bleujin", find.getString("userId")) ;
		
		assertTrue(conns[0] == find) ;
		
		client.disconnect() ;
		server.stopServer() ;
	}
	

}
