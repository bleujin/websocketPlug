package net.ion.chat.impl.listener;

import net.ion.chat.api.ChatListener;
import net.ion.chat.util.IMessagePacket;

public class DebugListener implements ChatListener {

	public void processClosed(ChatEvent event) {
	}

	public void processOpened(ChatEvent event) {
	}

	public void processPacket(ChatEvent event, IMessagePacket packet) {
		if (packet.isPing()) return ;
	}

}
