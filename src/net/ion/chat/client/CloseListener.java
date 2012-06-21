package net.ion.chat.client;

import net.ion.chat.api.CloseReason;

public interface CloseListener {
	public void onClose(CloseReason creason) ;
}
