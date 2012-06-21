package net.ion.chat.impl.plugin;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.chat.util.NormalMessagePacket;
import net.ion.framework.util.Debug;

public class AllBroadCastPlugIn extends BasePlugIn {

	@Override
	public void connectorStarted(IUserConnector connector) {
//		String userId = connector.getString("userId");
//		MessagePacket message = MessagePacket.create().inner("head").toParent().inner("body").put("message", userId + " login");
//		broadCast(message.forSend());
	}

	@Override
	public void connectorStopped(IUserConnector connector, CloseReason creason) {
//		String userId = connector.getString("userId");
//		MessagePacket message = MessagePacket.create().inner("head").toParent().inner("body").put("message", userId + " logout");
//		broadCastExcludeSelf(connector, message.forSend());
	}

	public void processPacket(PlugInResponse response, IUserConnector connector, IMessagePacket packet) {
		NormalMessagePacket message = NormalMessagePacket.load(packet.getFullString()).toRoot().inner("head").put("sender", connector.getString("userId"));
		broadCast(message);
	}

	
	private void broadCast(IMessagePacket packet) {
		for (UserConnector conn : getEngine().getAllConnectors()) {
			conn.send(packet);
		}
	}

}
