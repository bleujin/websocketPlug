package net.ion.chat.handler;

import net.ion.chat.api.ChatConstants;
import net.ion.chat.api.ChatHandler;
import net.ion.chat.server.ChatServer;
import net.ion.nradon.WebServer;
import net.ion.nradon.handler.logging.LoggingHandler;
import net.ion.nradon.handler.logging.SimpleLogSink;

public class ChatLoggingHandler implements ChatHandler{

	private LoggingHandler inner ;
	private ChatLoggingHandler() {
		this.inner = new LoggingHandler(new SimpleLogSink(ChatConstants.VAR_USERID));
	}

	public final static ChatLoggingHandler create(){
		return new ChatLoggingHandler() ;
	}

	public void set(ChatServer toonServer, WebServer wserver) {
		wserver.add(inner) ;
	}
	
}
