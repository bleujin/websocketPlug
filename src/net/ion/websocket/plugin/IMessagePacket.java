package net.ion.websocket.plugin;

import net.ion.websocket.common.api.WebSocketPacket;

public interface IMessagePacket {

	String getString(String path) ;
	
	String getFullString();

	IMessagePacket toRoot();

	WebSocketPacket forSend();

}
