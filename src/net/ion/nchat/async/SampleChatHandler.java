package net.ion.nchat.async;

import java.util.Collections;
import java.util.List;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

import net.ion.craken.Craken;
import net.ion.craken.EntryKey;
import net.ion.craken.LegContainer;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectId;
import net.ion.nchat.context.ChatCraken;
import net.ion.nchat.context.IMessagePacket;
import net.ion.nchat.context.NormalMessagePacket;
import net.ion.nchat.context.SerializedChatMessage;
import net.ion.nradon.AbstractWebSocketResource;
import net.ion.nradon.Radon;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.handler.event.ServerEventHandler;
import net.ion.nradon.handler.event.ServerEvent.EventType;

public class SampleChatHandler extends AbstractWebSocketResource implements ServerEventHandler {

	private List<WebSocketConnection> conns = ListUtil.newList();
	private LegContainer<SerializedChatMessage> container;

	public void onClose(WebSocketConnection conn) throws Exception {
		conns.remove(conn);
		NormalMessagePacket message = NormalMessagePacket.create().inner("body").put("message", conn.data("userId") + " logout");
		chat(conn, message);
	}

	public void onMessage(WebSocketConnection conn, String msg) throws Throwable {
		NormalMessagePacket message = NormalMessagePacket.load(msg) ;
		chat(conn, message);

	}

	public void onMessage(WebSocketConnection conn, byte[] arg1) throws Throwable {

	}

	public void onOpen(WebSocketConnection conn) throws Exception {
		conns.add(conn);
		NormalMessagePacket message = NormalMessagePacket.create().inner("body").put("message", conn.data("userId") + " login");
		chat(conn, message);
	}

	public void onPong(WebSocketConnection conn, String msg) throws Throwable {

	}

	private void chat(WebSocketConnection sender, NormalMessagePacket packet) {
		String msgId = new ObjectId().toString();
		container.newInstance(msgId).chat(packet.toRoot().inner("head").put("sender", sender.data("userId")).put("topicId", sender.data("topicId")) ).save();

	}
	
	void broadCastToRoom(SerializedChatMessage msg) {
		for (WebSocketConnection conn : conns) {
			if (conn.data("userId").equals(msg.sender())) continue;
			if (conn.data("topicId").equals(msg.topic())) {
				conn.send(msg.getFullString());
			}
		}
	}

	public void onEvent(EventType eventtype, Radon radon) {
		if (eventtype == EventType.START) {
			Craken craken = radon.getConfig().aradon().getServiceContext().getAttributeObject(ChatCraken.class.getCanonicalName(), ChatCraken.class).getCraken();
			this.container = craken.defineLeg(SerializedChatMessage.class);
			this.container.addListener(new ChatListener(this));
		}
	}

	@Listener
	public class ChatListener {

		private SampleChatHandler handler;

		public ChatListener(SampleChatHandler handler) {
			this.handler = handler;
		}

		@CacheEntryModified
		public void cacheModified(CacheEntryModifiedEvent<EntryKey, SerializedChatMessage> e) {
			if (e.isPre())
				return;

			handler.broadCastToRoom(e.getValue());
		}

	}

	public List<WebSocketConnection> getAllConnectors() {
		return Collections.unmodifiableList(conns) ;
	}

}
