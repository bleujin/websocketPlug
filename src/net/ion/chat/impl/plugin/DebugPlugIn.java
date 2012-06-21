package net.ion.chat.impl.plugin;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.Debug;

public class DebugPlugIn extends BasePlugIn {

	public void processPacket(PlugInResponse response, IUserConnector connector, IMessagePacket packet) {

//		MessagePacket message = MessagePacket.create().inner("head").put("command", "debug").toParent().inner("body").inner("users");
//		int idx = 0;
//		for (WebSocketConnector conn : getServer().getAllConnectors()) {
//			message.inner("user-" + idx).put("username", conn.getUsername()).put("host", conn.getRemoteHost()).put("port", conn.getRemotePort()).put("sessionid", conn.getSession().getSessionId()).toParent();
//			conn.sendPacket(message.forSend());
//			idx++;
//		}

		Debug.line('+', packet.getFullString());
	}

}
