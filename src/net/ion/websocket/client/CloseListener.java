package net.ion.websocket.client;

import net.ion.websocket.common.kit.CloseReason;

public interface CloseListener {

	public void onClose(CloseReason creason) ;
}
