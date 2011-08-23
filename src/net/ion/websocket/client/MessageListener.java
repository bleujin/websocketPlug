package net.ion.websocket.client;

import net.ion.framework.util.Debug;
import net.ion.websocket.plugin.IMessagePacket;

public interface MessageListener {

	MessageListener NOACTION = new MessageListener() {
		public void onMessage(IMessagePacket frame) {
		}
	};

	MessageListener DEBUG_OUT = new MessageListener() {
		public void onMessage(IMessagePacket frame) {
			Debug.debug(frame.getFullString()) ;
		}
	};

	void onMessage(IMessagePacket frame);

}
