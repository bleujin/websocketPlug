package net.ion.chat.api;

import net.ion.chat.handler.ChatEngine;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.radon.core.TreeContext;

public interface ChatPlugIn {

	void setEngine(ChatEngine talkEngine);
	
	ChatEngine getEngine() ;
	
	void engineStarted(TreeContext context);

	void engineStopped(TreeContext context);

	void connectorStarted(IUserConnector conn);

	void connectorStopped(IUserConnector conn, CloseReason creason);

	void processPacket(PlugInResponse response, IUserConnector conn, IMessagePacket packet);


}
