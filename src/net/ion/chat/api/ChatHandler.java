package net.ion.chat.api;

import net.ion.chat.server.ChatServer;
import net.ion.nradon.WebServer;

public interface ChatHandler {
	public void set(ChatServer toonServer, WebServer wserver) ;
}
