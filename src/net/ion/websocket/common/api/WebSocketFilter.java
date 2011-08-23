//	---------------------------------------------------------------------------
//	jWebSocket - Filter API
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
package net.ion.websocket.common.api;

import net.ion.websocket.common.kit.FilterResponse;


/**
 *
 * @author aschulze
 */
public interface WebSocketFilter {

	/**
	 *
	 * @param response
	 * @param connector
	 * @param aPacket
	 */
	void processPacketIn(FilterResponse response, WebSocketConnector connector);

	/**
	 *
	 * @param response
	 * @param source
	 * @param aTarget
	 * @param aPacket
	 */
	void processPacketOut(FilterResponse response, WebSocketConnector source);

	/**
	 *
	 * @param filterChain
	 */
	public void setFilterChain(WebSocketFilterChain filterChain);

	/**
	 * @return the filterChain
	 */
	public WebSocketFilterChain getFilterChain();
}
