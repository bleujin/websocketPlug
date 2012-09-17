package net.ion.nchat.context;

import java.io.Serializable;

import net.ion.craken.AbstractEntry;
import net.ion.craken.EntryKey;
import net.ion.craken.simple.EmanonKey;

public class SerializedChatMessage extends AbstractEntry<SerializedChatMessage> implements Serializable {

	private static final long serialVersionUID = 6153003775018649621L;
	private EntryKey key ;
	private NormalMessagePacket msgPacket ;
	private SerializedChatMessage(String msgid) {
		this.key = EmanonKey.create(msgid) ;
	} ;
	
	@Override
	public EntryKey key() {
		return key;
	}

	public SerializedChatMessage chat(NormalMessagePacket msgPacket) {
		this.msgPacket = msgPacket.toRoot() ;
		return this ;
	}

	public String sender() {
		if (msgPacket == null) return "" ;
		return msgPacket.getString("head.sender");
	}

	public String topic() {
		return msgPacket.getString("head.topicId");
	}

	public String getFullString() {
		return msgPacket.getFullString();
	}

}
