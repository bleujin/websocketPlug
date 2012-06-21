package net.ion.chat.util;

public class ChatMessage {

	public final static IMessagePacket create(String msg){
		if (msg.trim().startsWith("{")) return NormalMessagePacket.load(msg) ;
		else return PlainMessagePacket.create(msg) ;
	}
}
