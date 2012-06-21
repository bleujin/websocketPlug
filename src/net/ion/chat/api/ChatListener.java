package net.ion.chat.api;

import net.ion.chat.impl.listener.ChatEvent;
import net.ion.chat.util.IMessagePacket;

public interface ChatListener {
	
	public final static ChatListener NONE = new ChatListener() {
		
		public void processPacket(ChatEvent event, IMessagePacket packet) {
		}
		
		public void processOpened(ChatEvent event) {
		}
		
		public void processClosed(ChatEvent event) {
		}
	};
	
	public void processOpened(ChatEvent event);

	public void processPacket(ChatEvent event, IMessagePacket packet);

	public void processClosed(ChatEvent event);
}
