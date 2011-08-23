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
package net.ion.websocket.sample;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.kit.PlugInResponse;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.common.plugin.BasePlugInChain;
import net.ion.websocket.common.token.Token;

import org.apache.log4j.Logger;

/**
 * instantiates the chain of token plug-ins.
 * 
 * @author aschulze
 */
public class TokenPlugInChain extends BasePlugInChain {

	private static Logger log = Logging.getLogger(TokenPlugInChain.class);

	/**
	 * 
	 * @param aServer
	 */
	public TokenPlugInChain(WebSocketServer aServer) {
		super(aServer);
	}

	/**
	 * 
	 * @param aConnector
	 * @param aToken
	 * @return
	 */
	public PlugInResponse processToken(WebSocketConnector aConnector, Token aToken) {
		PlugInResponse lPluginResponse = new PlugInResponse();
		for (WebSocketPlugIn plugIn : getPlugIns()) {
			try {
				((TokenPlugIn) plugIn).processToken(lPluginResponse, aConnector, aToken);
			} catch (Exception ex) {
				log.error("(plugin '" + ((TokenPlugIn) plugIn).getNamespace() + "')" + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (lPluginResponse.isChainAborted()) {
				break;
			}
		}
		return lPluginResponse;
	}
}
