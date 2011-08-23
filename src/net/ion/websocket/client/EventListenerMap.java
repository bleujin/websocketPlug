package net.ion.websocket.client;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.plugin.IMessagePacket;

public class EventListenerMap {
	public enum Event {
		OnClose, OnMessage, OnOpen;
	}

	private List<MessageListener> mlisteners = ListUtil.newList();
	private List<CloseListener> clisteners = ListUtil.newList();

	public void register(MessageListener listener) {
		mlisteners.add(listener);
	}

	public void register(CloseListener listener) {
		clisteners.add(listener);
	}

	public void onMessage(IMessagePacket frame) {
		for (MessageListener li : mlisteners) {
			li.onMessage(frame);
		}
	}
	
	public void onClose() {
		for (CloseListener li : clisteners) {
			li.onClose(CloseReason.SHUTDOWN);
		}
	}

}