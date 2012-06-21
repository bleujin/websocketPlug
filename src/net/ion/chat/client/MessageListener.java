package net.ion.chat.client;

import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.Debug;

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
