package net.ion.chat.client;

import java.util.List;

import net.ion.chat.api.CloseReason;
import net.ion.chat.util.IMessagePacket;
import net.ion.chat.util.NormalMessagePacket;
import net.ion.framework.util.ListUtil;

public class EventListenerMap {
	
	public enum Event {
		OnClose, OnMessage, OnOpen;
	}

	private List<MessageListener> mlisteners = ListUtil.newList();
	private List<CloseListener> clisteners = ListUtil.newList();
	private IMessagePacket lastPacket = NormalMessagePacket.EMPTY;
	
	public void register(MessageListener listener) {
		mlisteners.add(listener);
	}

	public void register(CloseListener listener) {
		clisteners.add(listener);
	}

	public void onMessage(IMessagePacket messagePacket) {
		this.lastPacket = messagePacket ;
		for (MessageListener li : mlisteners) {
			li.onMessage(messagePacket);
		}
	}
	
	public IMessagePacket getLastPacket() {
		return lastPacket ;
	}
	
	public void onClose() {
		for (CloseListener li : clisteners) {
			li.onClose(CloseReason.SHUTDOWN);
		}
	}

}