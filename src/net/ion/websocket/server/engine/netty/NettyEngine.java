package net.ion.websocket.server.engine.netty;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javolution.util.FastList;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.config.ServerConstants;
import net.ion.websocket.common.engine.BaseEngine;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.server.NettyEnginePipeLineFactory;
import net.ion.websocket.server.engine.EngineConfig;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class NettyEngine extends BaseEngine {

	private static Logger log = Logging.getLogger(NettyEngine.class);
	private volatile boolean isRunning = false;
	private static final ChannelGroup allChannels = new DefaultChannelGroup("jWebSocket-NettyEngine");
	private Channel channel = null;

	
	private int port ;
	public NettyEngine(EngineConfiguration configuration) {
		this(configuration, configuration.getPort());
	}
	public NettyEngine(EngineConfiguration configuration, int port) {
		super(configuration);
		this.port = port;
	}
	
	public final static NettyEngine test(){
		return new NettyEngine(EngineConfig.test(9000)) ;
	}
	
	public final static NettyEngine test(int port){
		return new NettyEngine(EngineConfig.test(port)) ;
	}
	

	
	@Override
	public void startEngine() throws WebSocketException {

		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyEnginePipeLineFactory(this));
		// Bind and start to accept incoming connections.
		channel = bootstrap.bind(new InetSocketAddress(port));

		// set the timeout value if only it's greater than 0 in configuration
		if (getConfiguration().getTimeout() > 0) {
			channel.getConfig().setConnectTimeoutMillis(getConfiguration().getTimeout());
		}


		// fire the engine start event
		engineStarted();

		allChannels.add(channel);

		isRunning = true;

		if (log.isInfoEnabled()) {
			log.info("Netty engine '" + getId() + "' started.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopEngine(CloseReason creason) throws WebSocketException {
		log.debug("Stopping Netty engine '" + getId() + "'...");
		for (WebSocketConnector conn : getConnectors().getAllConnectors()) {
			conn.stopConnector(CloseReason.SHUTDOWN) ;
		}
		
		isRunning = false;

		
		super.stopEngine(creason);
		engineStopped();

		// Added by Alex 2010-08-09
		if (channel != null) {
			channel.close();
			channel.getFactory().releaseExternalResources(); // Moved from last line to here by Alex 2010-11-23 to prevent exceptions
		}
		ChannelGroupFuture future = allChannels.close();
		future.awaitUninterruptibly(2, TimeUnit.SECONDS);
		
		// if (channel != null) channel.getFactory().releaseExternalResources();
	}

	@Override
	public void connectorStarted(WebSocketConnector connector) {
		super.connectorStarted(connector);

	}

	@Override
	public void connectorStopped(WebSocketConnector connector, CloseReason creason) {
		super.connectorStopped(connector, creason);
	}

	@Override
	public boolean isAlive() {
		if (isRunning) {
			return true;
		} else {
			return false;
		}
	}

}
