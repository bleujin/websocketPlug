//	---------------------------------------------------------------------------
//	jWebSocket - Result from a filter in the filter chain
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package net.ion.websocket.common.kit;

import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.plugin.MessagePacket;

public class FilterResponse {

	private boolean isRejected = false;
	private String message ;
	/**
	 * Returns if a filter in the filter chain has rejected a message.
	 * @return the chainAborted
	 */
	public Boolean isRejected() {
		return isRejected;
	}

	/**
	 * Signals that a message has to be rejected and that the filter chain
	 * was aborted.
	 */
	public void rejectMessage(String message) {
		this.isRejected = true;
		this.message = message ;
	}

	/**
	 * Signals that a message may be relayed to further filters, the server or
	 * clients, depending on its direction.
	 */
	public void relayMessage(String message) {
		this.isRejected = false;
		this.message = message ;
	}

	public WebSocketPacket getMessage() {
		return MessagePacket.create().inner("body").put("message", message).forSend() ;
	}


}
