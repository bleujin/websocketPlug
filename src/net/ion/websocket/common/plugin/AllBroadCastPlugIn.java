package net.ion.websocket.common.plugin;

import net.ion.framework.util.Debug;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;
import net.ion.websocket.plugin.MessagePacket;

public class AllBroadCastPlugIn extends BasePlugIn {

	@Override
	public void connectorStarted(WebSocketConnector connector) {
//		String userId = connector.getString("userId");
//		MessagePacket message = MessagePacket.create().inner("head").toParent().inner("body").put("message", userId + " login");
//		broadCast(message.forSend());
	}

	@Override
	public void connectorStopped(WebSocketConnector connector, CloseReason creason) {
//		String userId = connector.getString("userId");
//		MessagePacket message = MessagePacket.create().inner("head").toParent().inner("body").put("message", userId + " logout");
//		broadCastExcludeSelf(connector, message.forSend());
	}

	@Override
	public void processPacket(PlugInResponse response, WebSocketConnector connector, WebSocketPacket packet) {
		MessagePacket message = MessagePacket.load(packet.getUTF8()).toRoot().inner("head").put("sender", connector.getString("userId"));
		broadCast(message.forSend());
		Debug.line(message.getFullString()) ;
	}

	
	private void broadCast(WebSocketPacket packet) {
		for (WebSocketConnector conn : getServer().getAllConnectors()) {
			conn.sendPacket(packet);
		}
	}

}
