package net.ion.websocket.client;

import java.net.URI;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;

/**
 * A factory for creating WebSocket clients. The entry point for creating and connecting a client. Can and should be used to create multiple instances.
 * 
 * @author <a href="http://www.pedantique.org/">Carl Bystr&ouml;m</a>
 */
public class WebSocketClientFactory {


	public static WebSocketClient newClient(final URI url, final WebSocketCallback callback) {
		NioClientSocketChannelFactory socketChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

		ClientBootstrap bootstrap = new ClientBootstrap(socketChannelFactory);

		String protocol = url.getScheme();
		if (!protocol.equals("ws") && !protocol.equals("wss")) {
			throw new IllegalArgumentException("Unsupported protocol: " + protocol);
		}

		final WebSocketClientHandler clientHandler = new WebSocketClientHandler(bootstrap, url, callback);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", new HttpResponseDecoder());
				pipeline.addLast("encoder", new HttpRequestEncoder());
				pipeline.addLast("ws-handler", clientHandler);
				return pipeline;
			}
		});

		return clientHandler;
	}

}
