package net.ion.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import junit.framework.TestCase;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.PacketConstant;
import net.ion.websocket.common.api.ServerConfiguration;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.server.DefaultServer;
import net.ion.websocket.server.context.ServiceContext;
import net.ion.websocket.server.engine.netty.NettyEngine;

public class TestBaseWebSocket extends TestCase implements PacketConstant {
	protected DefaultServer server;
	protected URI uri = null;
	protected WebSocketPlugIn plugin;

	public static ServerConfiguration TEST_CONFIG = new ServerConfiguration() {

		public String getName() {
			return "0";
		}

		public String getId() {
			return "test0";
		}

		public String getURIPath() {
			return "/{userId}/{sessionId}/{params}";
		}

		public String getJar() {
			return null;
		}
	};


	@Override
	protected void setUp() throws Exception {
		super.setUp();

		server = new DefaultServer(TEST_CONFIG, NettyEngine.test(), ServiceContext.createRoot());
		uri = new URI("ws://127.0.0.1:9000/;timeout=3600000");
		//server.startServer();
	}

	@Override
	protected void tearDown() throws Exception {
		// new InfinityThread().startNJoin() ;
		server.stopServer();
		super.tearDown();
	}

	protected void connectAsUser(SyncMockClient c, String userId) throws URISyntaxException, InvalidKeyException {
		long authKey = 123455 ;
		
		c.connect(new URI("ws://127.0.0.1:9000/" + userId + "/" + authKey ));
	}

}
