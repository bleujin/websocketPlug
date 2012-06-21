package net.ion.chat.server;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.ion.chat.api.ChatConstants;
import net.ion.chat.handler.ChatEngine;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.WebServer;
import net.ion.nradon.WebServers;

public class ChatServer {
	
	private WebServer server;
	private ChatEngine engine ;
	private boolean lived;

	private ChatServer(ChatEngine engine){
		this.engine = engine ;
	}
	
	public final static ChatServer create(ChatEngine engine){
		ChatServer server = new ChatServer(engine) ;
		return server ;
	}

	public ChatServer start() throws Exception {
		
		String toonHome = System.getProperty(ChatConstants.TOON_HOME_DIR) ;
		if (StringUtil.isBlank(toonHome)){
			System.setProperty(ChatConstants.TOON_HOME_DIR, new File("").getAbsolutePath()) ;
		}
		
		this.server = WebServers.createWebServer(Executors.newCachedThreadPool(), engine.getConfiguration().getPort()) ;
		engine.set(this, this.server) ;
		Runtime.getRuntime().addShutdownHook(new ShutdownThread(this)) ;

		server.start() ;
		this.lived = true ;
		return this ;
	}

	
	public ChatServer stop() throws IOException { // Stop web server background thread. This returns immediately, but the webserver may still be shutting down. To wait until it's fully stopped, use {@link #join()}.
		// try {if (engine != null) engine.shutdown(); } catch(Throwable ignore){ignore.printStackTrace(); } ;
		if (engine != null) engine.shutdown();
		if (server != null) server.stop() ;
		this.lived = false ;
		return this ;
	}

	public boolean isAlive() {
		return lived;
	}

	public ChatEngine getEngine() {
		return engine;
	}

}


class ShutdownThread extends Thread{

	private ChatServer server ;
	public ShutdownThread(ChatServer server) {
		this.server = server ;
	}

	@Override
	public void run(){
		try {
			server.stop() ;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
