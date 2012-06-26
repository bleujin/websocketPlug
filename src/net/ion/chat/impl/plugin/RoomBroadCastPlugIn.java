package net.ion.chat.impl.plugin;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.chat.util.NormalMessagePacket;

public class RoomBroadCastPlugIn extends BasePlugIn {

	@Override
	public void connectorStarted(IUserConnector connector) {
		NormalMessagePacket message = NormalMessagePacket.create().inner("head").toParent().inner("body").put("message", connector.getUserId() + " login");
		broadCastExcludeSelf(connector, message);
	}

	@Override
	public void connectorStopped(IUserConnector connector, CloseReason creason) {
		NormalMessagePacket message = NormalMessagePacket.create().inner("head").toParent().inner("body").put("message", connector.getUserId() + " logout");
		broadCastExcludeSelf(connector, message);
	}

	public void processPacket(PlugInResponse response, IUserConnector connector, IMessagePacket packet) {
		NormalMessagePacket message = NormalMessagePacket.load(packet.getFullString()).toRoot().inner("head").put("sender", connector.getString("userId"));
		broadCastExcludeSelf(connector, message);
	}

	
	private void broadCastExcludeSelf(IUserConnector connector, IMessagePacket packet) {
		for (UserConnector conn : getEngine().getAllConnectors()) {
			if (conn.equals(connector)) continue ;
			if (conn.getTopicId().equals(connector.getTopicId())) {
				conn.send(packet);
			}
		}
	}

}
