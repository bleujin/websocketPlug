package net.ion.nchat.async;

import java.util.List;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

import net.ion.craken.Craken;
import net.ion.craken.EntryKey;
import net.ion.craken.LegContainer;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nchat.async.SampleChatHandler.ChatListener;
import net.ion.nchat.context.ChatCraken;
import net.ion.nchat.context.SerializedChatMessage;
import net.ion.nradon.AbstractEventSourceResource;
import net.ion.nradon.EventSourceConnection;
import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEventHandler;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.nradon.netty.contrib.EventSourceMessage;

public class SampleEventSource extends AbstractEventSourceResource implements ServerEventHandler {

	private List<EventSourceConnection> conns = ListUtil.newList() ;
	private LegContainer<SerializedChatMessage> container;
	public void onClose(EventSourceConnection conn) throws Exception {
		conns.remove(conn) ;
	}

	public void onOpen(EventSourceConnection conn) throws Exception {
		conns.add(conn) ;
	}
	
	public void onEvent(EventType eventtype, Radon radon) {
		if (eventtype == EventType.START) {
			Craken craken = radon.getConfig().aradon().getServiceContext().getAttributeObject(ChatCraken.class.getCanonicalName(), ChatCraken.class).getCraken();
			this.container = craken.defineLeg(SerializedChatMessage.class);
			this.container.addListener(new ChatListener(this));
		}
	}

	void sendMsg(SerializedChatMessage msg) {
		for(EventSourceConnection conn : conns){
			if ( "__all".equals(msg.topic()) || conn.data("topicId").equals(msg.topic())){
				conn.send(new EventSourceMessage(msg.getFullString())) ;
			}
		}
	}
	
	@Listener
	public class ChatListener {

		private SampleEventSource handler;

		public ChatListener(SampleEventSource handler) {
			this.handler = handler;
		}

		@CacheEntryModified
		public void cacheModified(CacheEntryModifiedEvent<EntryKey, SerializedChatMessage> e) {
			if (e.isPre())
				return;
			handler.sendMsg(e.getValue());
		}

	}



}
