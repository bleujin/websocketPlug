package net.ion.chat.impl.plugin;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserBean;
import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.Debug;

public class SessionAuthPlugIn extends BasePlugIn {

	@Override
	public void connectorStarted(IUserConnector conn) {
		if (!conn.isAuthUser()) {
			conn.stopConnector(CloseReason.define(conn.getUserId() + "[" + conn.getAuthId() + "] is not authficated."));
			return;
		}

		
		UserBean userBean = UserBean.create(conn.getUserId());
		conn.data(UserBean.class.getCanonicalName(), userBean) ;
		
		Debug.line(userBean) ;
	}

	public void connectorStopped(IUserConnector conn, CloseReason creason) {
	}

	public void processPacket(PlugInResponse response, IUserConnector connector, IMessagePacket packet) {

		// connect, out, reconnect
	}

}
