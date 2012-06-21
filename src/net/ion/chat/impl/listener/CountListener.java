package net.ion.chat.impl.listener;

import java.util.concurrent.atomic.AtomicInteger;

import net.ion.chat.api.ChatListener;
import net.ion.chat.util.IMessagePacket;

public class CountListener implements ChatListener {

	private AtomicInteger count = new AtomicInteger() ;
	public void processClosed(ChatEvent event) {
		count.incrementAndGet() ;
	}

	public void processOpened(ChatEvent event) {
		count.incrementAndGet() ;
	}

	public void processPacket(ChatEvent event, IMessagePacket packet) {
		count.incrementAndGet() ;
	}

	public int getCount() {
		return count.get() ;
	}
}
