package net.ion.chat.handler;

import net.ion.chat.api.ChatHandler;
import net.ion.chat.server.ChatServer;
import net.ion.nradon.WebServer;
import net.ion.nradon.handler.StaticFileHandler;

public class ClientFileHandler implements ChatHandler{

	private StaticFileHandler inner ;
	public ClientFileHandler() {
		this.inner = new StaticFileHandler("./resource/toonweb/");
	}

	public final static ClientFileHandler create(){
		return new ClientFileHandler() ;
	}

	public void set(ChatServer toonServer, WebServer wserver) {
		wserver.add(inner) ;
	}
	
	
}
