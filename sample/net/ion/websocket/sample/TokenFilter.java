//	---------------------------------------------------------------------------
//	jWebSocket - TokenFilter Implementation
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
package net.ion.websocket.sample;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.filter.BaseFilter;
import net.ion.websocket.common.kit.FilterResponse;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.common.token.Token;

import org.apache.log4j.Logger;

/**
 * 
 * @author aschulze
 */
public class TokenFilter extends BaseFilter {

	private static Logger log = Logging.getLogger(TokenFilter.class);

	public TokenFilter(String aId) {
		super(aId);
	}

	@Override
	public void processPacketIn(FilterResponse aResponse, WebSocketConnector aConnector, WebSocketPacket aPacket) {
	}

	@Override
	public void processPacketOut(FilterResponse aResponse, WebSocketConnector aSource, WebSocketConnector aTarget, WebSocketPacket aPacket) {
	}

	public void processTokenIn(FilterResponse aResponse, WebSocketConnector aConnector, Token aToken) {
	}

	public void processTokenOut(FilterResponse aResponse, WebSocketConnector aSource, WebSocketConnector aTarget, Token aToken) {
	}

	/**
	 * 
	 * @return
	 */
	public TokenServer getServer() {
		TokenServer lServer = null;
		TokenFilterChain filterChain = (TokenFilterChain) getFilterChain();
		if (filterChain != null) {
			lServer = (TokenServer) filterChain.getServer();
		}
		return lServer;
	}
}
