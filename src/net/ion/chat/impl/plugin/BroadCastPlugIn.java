package net.ion.chat.impl.plugin;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.Closure;
import net.ion.framework.util.Debug;
import net.ion.radon.core.TreeContext;

public class BroadCastPlugIn extends BasePlugIn{

	public void connectorStarted(IUserConnector conn) {
		each(new Closure<IUserConnector>() {
			public void execute(IUserConnector conn) {
				conn.send(conn.getUserId() + " login..") ;
			}
		}) ;
		Debug.line(conn.getUserId() + " login..") ;
	}

	public void connectorStopped(IUserConnector conn, CloseReason creason) {
		each(new Closure<IUserConnector>() {
			public void execute(IUserConnector conn) {
				conn.send(conn.getUserId() + " logout..") ;
			}
		}) ;
		Debug.line(conn.getUserId() + " logout..") ;
	}

	public void engineStarted(TreeContext context) {
		Debug.line("engine started") ;
	}

	public void engineStopped(TreeContext context) {
		Debug.line("engine stoped") ;
	}

	public void processPacket(PlugInResponse response, IUserConnector conn, final IMessagePacket packet) {
		each(new Closure<IUserConnector>() {
			public void execute(IUserConnector conn) {
				conn.send(conn.getUserId() + " : " + packet.getFullString()) ;
			}
		}) ;
	}
}
