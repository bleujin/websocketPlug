//	---------------------------------------------------------------------------
//	jWebSocket - Chain of Token Plug-Ins
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
package net.ion.websocket.common.plugin;

import net.ion.websocket.common.kit.PlugInResponse;
import net.ion.websocket.common.api.WebSocketPlugIn;
import org.apache.log4j.Logger;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.common.token.Token;

/**
 * instantiates the chain of token plug-ins.
 * 
 * @author aschulze
 */
public class TokenPlugInChain extends BasePlugInChain {

	private static Logger logger = Logging.getLogger(TokenPlugInChain.class);

	/**
	 * 
	 * @param server
	 */
	public TokenPlugInChain(WebSocketServer server) {
		super(server);
	}

	/**
	 * 
	 * @param connector
	 * @param token
	 * @return
	 */
	public PlugInResponse processToken(WebSocketConnector connector, Token token) {
		PlugInResponse plugInResponse = new PlugInResponse();
		String ins = token.getNS();
		// tokens without namespace are not accepted anymore since jWebSocket 1.0a11
		if (ins != null) {
			for (WebSocketPlugIn lPlugIn : getPlugIns()) {
				try {
					TokenPlugIn tokenPlugIn = ((TokenPlugIn) lPlugIn);
					if (ins.equals(tokenPlugIn.getNamespace())) {
						tokenPlugIn.processToken(plugInResponse, connector, token);
					}
				} catch (Exception ex) {
					logger.error("(plug-in '" + ((TokenPlugIn) lPlugIn).getNamespace() + "') " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + ", token: " + token.toString());
				}
				if (plugInResponse.isChainAborted()) {
					break;
				}
			}
		}
		return plugInResponse;
	}
}
