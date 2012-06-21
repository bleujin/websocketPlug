package net.ion.chat.impl.listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.ion.chat.api.ChatListener;
import net.ion.chat.util.IMessagePacket;
import net.ion.chat.util.NormalMessagePacket;

public class ChannelListener implements ChatListener {

	private final int capacity ;
	private final BlockingQueue<ServerEvent> queue;

	private WorkerThread thread;
	public ChannelListener(int capacity, ChatListener... handler) {
		this.capacity = Math.max(capacity, 1000) ;
		this.queue = new ArrayBlockingQueue<ServerEvent>(this.capacity);

		this.thread = new WorkerThread(handler, this);
		this.thread.start() ;
	}

	public void processOpened(ChatEvent event) {
		putEvent(ServerEvent.opened(event));
	}

	private void putEvent(ServerEvent sevent) {
		try {
			queue.put(sevent);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
	}

	public void processPacket(ChatEvent event, IMessagePacket packet) {
		putEvent(ServerEvent.process(event, packet));
	}

	public void processClosed(ChatEvent event) {
		putEvent(ServerEvent.closed(event));
	}

	public ServerEvent takeEvent() throws InterruptedException {
		return queue.take();
	}

	
	public ChannelListener(ChatListener l1, int capacity) {
		this(capacity, l1) ;
	}
	public ChannelListener(ChatListener l1, ChatListener l2, int capacity) {
		this(capacity, l1, l2) ;
	}
	public ChannelListener(ChatListener l1, ChatListener l2, ChatListener l3, int capacity) {
		this(capacity, l1, l2, l3) ;
	}
	public ChannelListener(ChatListener l1, ChatListener l2, ChatListener l3, ChatListener l4, int capacity) {
		this(capacity, l1, l2, l3, l4) ;
	}
	public ChannelListener(ChatListener l1, ChatListener l2, ChatListener l3, ChatListener l4, ChatListener l5, int capacity) {
		this(capacity, l1, l2, l3, l4, l5) ;
	}
}

enum WhenEvent{
	OPEN , PROCESS , CLOSE, UNKNOWN ;
}

class ServerEvent {

	private ChatEvent event ;
	private IMessagePacket packet ;
	private WhenEvent wevent ;
	private ServerEvent(ChatEvent event, IMessagePacket packet, WhenEvent wevent) {
		this.event = event ;
		this.packet = packet ;
		this.wevent = wevent ;
	}

	public static ServerEvent process(ChatEvent event, IMessagePacket packet) {
		return new ServerEvent(event, packet, WhenEvent.PROCESS);
	}

	public static ServerEvent closed(ChatEvent event) {
		return new ServerEvent(event, NormalMessagePacket.EMPTY, WhenEvent.CLOSE);
	}

	public static ServerEvent opened(ChatEvent event) {
		return new ServerEvent(event, NormalMessagePacket.EMPTY, WhenEvent.OPEN);
	}

	public IMessagePacket getPacket(){
		return packet ;
	}
	
	public ChatEvent getEvent(){
		return event ;
	}

	public void handleEvent(ChatListener listener) {
		if (this.wevent == WhenEvent.CLOSE){
			listener.processClosed(event) ;
		} else if (this.wevent == WhenEvent.OPEN){
			listener.processOpened(event) ;
		} else if (this.wevent == WhenEvent.PROCESS){
			listener.processPacket(event, packet) ;
		}
	}
	
}
