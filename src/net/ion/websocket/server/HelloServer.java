package net.ion.websocket.server;

import java.net.URI;

import net.ion.framework.util.InfinityThread;
import net.ion.websocket.common.PacketConstant;
import net.ion.websocket.common.plugin.AllBroadCastPlugIn;
import net.ion.websocket.common.plugin.LogPlugIn;
import net.ion.websocket.server.engine.netty.NettyEngine;

public class HelloServer implements PacketConstant {

	public static void main(String[] args) throws Exception {
		URI uri = new URI("ws://127.0.0.1:9000/;timeout=3600000");
		DefaultServer server = new DefaultServer(NettyEngine.test());
		((DefaultServerConfiguration)server.getConfiguration()).testURIPath("/{userId}/{sessionId}/{params}") ;  // only test 
		
		server.getPlugInChain().addPlugIn(new AllBroadCastPlugIn());
		server.getPlugInChain().addPlugIn(new LogPlugIn()) ;
		server.startServer();
//		server.getPlugInChain().addPlugIn(new SystemPlugIn());

	}

	
}
