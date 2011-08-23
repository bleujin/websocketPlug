package net.ion.websocket.common;

import java.util.concurrent.atomic.AtomicLong;

import net.ion.websocket.common.api.ObjectId;

public class MessagePacketId {

	private static AtomicLong current = new AtomicLong(1000000) ;
	
	public static String create() {
		return new ObjectId().toString();
	}

}
