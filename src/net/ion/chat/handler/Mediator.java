package net.ion.chat.handler;

import net.ion.chat.server.UserBean;
import net.ion.chat.util.IMessagePacket;

public class Mediator {

	private ChatEngine engine ;
	private Mediator(ChatEngine engine) {
		this.engine = engine ;
	}

	public static Mediator create(ChatEngine engine) {
		return new Mediator(engine);
	}

	public void saveAt(UserBean sender, String targetId, IMessagePacket msg) {
	}
}