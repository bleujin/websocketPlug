package net.ion.chat.impl.listener;

import net.ion.chat.handler.ChatEngine;
import net.ion.chat.server.IUserConnector;


public class ChatEvent {

	private ChatEngine engine ;
	private IUserConnector conn ;

	private ChatEvent(IUserConnector conn, ChatEngine engine) {
		this.conn = conn;
		this.engine = engine;
	}
	
	public static ChatEvent create(IUserConnector conn, ChatEngine engine){
		return new ChatEvent(conn, engine) ;
	}
	
	public ChatEngine getEngine() {
		return engine;
	}

	public IUserConnector getConnector() {
		return conn;
	}

}
