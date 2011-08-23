package net.ion.websocket.common.plugin;

import net.ion.framework.util.Debug;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.PlugInResponse;

public class DebugPlugIn extends BasePlugIn {

	@Override
	public void processPacket(PlugInResponse response, WebSocketConnector connector, WebSocketPacket packet) {

//		MessagePacket message = MessagePacket.create().inner("head").put("command", "debug").toParent().inner("body").inner("users");
//		int idx = 0;
//		for (WebSocketConnector conn : getServer().getAllConnectors()) {
//			message.inner("user-" + idx).put("username", conn.getUsername()).put("host", conn.getRemoteHost()).put("port", conn.getRemotePort()).put("sessionid", conn.getSession().getSessionId()).toParent();
//			conn.sendPacket(message.forSend());
//			idx++;
//		}

		Debug.line('+', packet.getUTF8());
	}

}
