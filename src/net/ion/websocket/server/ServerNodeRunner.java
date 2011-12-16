package net.ion.websocket.server;

import java.io.File;

import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.Options;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.aradon.EmbedAradonServer;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.config.ListenerInfo;
import net.ion.websocket.common.config.PlugInInfo;
import net.ion.websocket.common.config.ServerConfigParser;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.server.engine.netty.NettyEngine;

import org.apache.commons.configuration.ConfigurationException;

public class ServerNodeRunner {

	private DefaultServer server ;
	private EmbedAradonServer embedAradon ;
	
	public ServerNodeRunner(Options option) throws ConfigurationException, WebSocketException, InstanceCreationException{
		String configPath = option.getString("config", "resource/config/server-config.xml") ;
		String baseDir = option.getString("basedir", "./") ;

		if (! new File(configPath).exists() ) throw new ConfigurationException("not found config file : " + option) ;
		XMLConfig root = XMLConfig.create(configPath) ;
		ServerConfigParser config = ServerConfigParser.parse(root, baseDir) ;
		
		init(option, config) ;
	}
	
	private void init(Options option, ServerConfigParser config) throws WebSocketException{
		
		EngineConfiguration engineConfig = config.getEngineConfiguration(option.getInt("port", 0));
		
		NettyEngine engine = new NettyEngine(engineConfig, option.getInt("port", engineConfig.getPort())) ;

		this.server = new DefaultServer(config.getServerConfiguration(option.getString("sid", "")), engine, config.getContext());
		for (PlugInInfo pinfo : config.getPlugins()) {
			server.getPlugInChain().addPlugIn(pinfo.getPlugIn()) ;
		}

//		for (FilterInfo finfo : config.getFilter()) {
//			server.getFilterChain().addFilter(finfo.getFilter()) ;
//		}

		for (ListenerInfo linfo : config.getListener()) {
			server.addListener(linfo.getListener()) ;
		}
		this.embedAradon = new EmbedAradonServer(config.getAradonConfiguration());
	}

	public void start() throws Exception{
		server.startServer() ;
		final ServerNodeRunner thisServer = this ;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					thisServer.stop() ;
				} catch (WebSocketException ignore) {
					ignore.printStackTrace();
				} catch (InstanceCreationException ignore) {
					ignore.printStackTrace();
				}
			}
		});
		
		embedAradon.start(server) ;
		
	}
	
	public DefaultServer getServer() {
		return server;
	}

	public void stop() throws InstanceCreationException, WebSocketException {
		// if (! server.isAlive()) return ;
		embedAradon.stop() ;
		server.stopServer() ;
		
	}

	public Aradon getAradon() {
		return embedAradon.getAradon() ;
	}
	

	public static void main(String[] args) throws Exception{
		Options option = new Options(args) ;
		
		ServerNodeRunner runner = new ServerNodeRunner(option);
		runner.start() ;
	}

}
