//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 Innotrade GmbH
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
package net.ion.websocket.common.listener;

import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.token.Token;

/**
 * Interface for the Token WebSocket listeners based on the low level listeners.
 * @author aschulze
 */
public interface WebSocketServerTokenListener extends WebSocketServerListener {

	/**
	 * This method is invoked when a token (JSON, CSV or XML) from a client is
	 * received. The event provides getter for the server and the connector to
	 * send responses to back the client.
	 * @param aEvent
	 * @param aToken
	 */
	public void processToken(WebSocketServerTokenEvent aEvent, Token aToken);
}
