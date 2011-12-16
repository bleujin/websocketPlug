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
package net.ion.websocket.common.filter;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.config.FilterConfiguration;
import net.ion.websocket.common.kit.FilterResponse;
import net.ion.websocket.server.TokenServer;
import net.ion.websocket.common.token.Token;

/**
 * 
 * @author aschulze
 */
public class TokenFilter extends BaseFilter {

	public TokenFilter(FilterConfiguration configuration) {
		super(configuration);
	}

	@Override
	public void processPacketIn(FilterResponse response, WebSocketConnector connector, WebSocketPacket packet) {
	}

	@Override
	public void processPacketOut(FilterResponse response, WebSocketConnector source, WebSocketConnector target, WebSocketPacket packet) {
	}

	public void processTokenIn(FilterResponse response, WebSocketConnector aConnector, Token aToken) {
	}

	public void processTokenOut(FilterResponse response, WebSocketConnector source, WebSocketConnector target, Token token) {
	}

	/**
	 * 
	 * @return
	 */
	public TokenServer getServer() {
		TokenServer server = null;
		TokenFilterChain filterChain = (TokenFilterChain) getFilterChain();
		if (filterChain != null) {
			server = (TokenServer) filterChain.getServer();
		}
		return server;
	}
}
