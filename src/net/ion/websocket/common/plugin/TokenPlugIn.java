//	---------------------------------------------------------------------------
//	jWebSocket - Wrapper for Token based PlugIns (Convenience Class)
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
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.async.IOFuture;
import net.ion.websocket.common.config.PluginConfiguration;
import net.ion.websocket.common.kit.BroadcastOptions;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.token.Token;
import net.ion.websocket.server.TokenServer;

/**
 * 
 * @author aschulze
 */
public class TokenPlugIn extends BasePlugIn {

	private String mNamespace = null;

	/*
	 * public TokenPlugIn() { }
	 */

	/**
	 * 
	 * @param pconfig
	 */
	public TokenPlugIn(PluginConfiguration pconfig) {
		super(pconfig);
	}

	@Override
	public void engineStarted(WebSocketEngine engine) {
	}

	@Override
	public void engineStopped(WebSocketEngine engine) {
	}

	/**
	 * 
	 * @param connector
	 */
	@Override
	public void connectorStarted(WebSocketConnector connector) {
	}

	/**
	 * 
	 * @param response
	 * @param connector
	 * @param token
	 */
	public void processToken(PlugInResponse response, WebSocketConnector connector, Token token) {
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public Token invoke(WebSocketConnector connector, Token token) {
		return null;
	}

	@Override
	public void processPacket(PlugInResponse response, WebSocketConnector connector, WebSocketPacket dataPacket) {
		//
	}

	/**
	 * 
	 * @param connector
	 */
	@Override
	public void connectorStopped(WebSocketConnector connector, CloseReason creason) {
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return mNamespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.mNamespace = namespace;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public TokenServer getServer() {
		return (TokenServer) super.getServer();
	}

	/**
	 * Convenience method, just a wrapper for token server method <tt>createResponse</tt> to simplify token plug-in code.
	 * 
	 * @param token
	 * @return
	 */
	public Token createResponse(Token token) {
		TokenServer server = getServer();
		if (server != null) {
			return server.createResponse(token);
		} else {
			return null;
		}
	}

	/**
	 * Convenience method, just a wrapper for token server method <tt>createAccessDenied</tt> to simplify token plug-in code.
	 * 
	 * @param token
	 * @return
	 */
	public Token createAccessDenied(Token token) {
		TokenServer server = getServer();
		if (server != null) {
			return server.createAccessDenied(token);
		} else {
			return null;
		}
	}

	/**
	 * Convenience method, just a wrapper for token server method <tt>sendToken</tt> to simplify token plug-in code.
	 * 
	 * @param source
	 * @param target
	 * @param token
	 */
	public void sendToken(WebSocketConnector source, WebSocketConnector target, Token token) {
		TokenServer server = getServer();
		if (server != null) {
			server.sendToken(source, target, token);
		}
	}

	/**
	 * Sends the the given token asynchronously and returns the future object to keep track of the send operation
	 * 
	 * @param source
	 *            the source connector
	 * @param target
	 *            the target connector
	 * @param token
	 *            the token object
	 * @return the I/O future object
	 */
	public IOFuture sendTokenAsync(WebSocketConnector source, WebSocketConnector target, Token token) {
		TokenServer server = getServer();
		if (server != null) {
			return server.sendTokenAsync(source, target, token);
		}
		return null;
	}

	/**
	 * Convenience method, just a wrapper for token server method <tt>sendToken</tt> to simplify token plug-in code.
	 * 
	 * @param source
	 * @param token
	 */
	public void broadcastToken(WebSocketConnector source, Token token) {
		TokenServer server = getServer();
		if (server != null) {
			server.broadcastToken(source, token);
		}
	}

	/**
	 * 
	 * @param source
	 * @param token
	 * @param broadcastOptions
	 */
	public void broadcastToken(WebSocketConnector source, Token token, BroadcastOptions broadcastOptions) {
		TokenServer server = getServer();
		if (server != null) {
			server.broadcastToken(source, token, broadcastOptions);
		}
	}

	/**
	 * 
	 * @param connector
	 * @param inToken
	 * @param errCode
	 * @param message
	 */
	public void sendErrorToken(WebSocketConnector connector, Token inToken, int errCode, String message) {
		TokenServer server = getServer();
		if (server != null) {
			server.sendErrorToken(connector, inToken, errCode, message);
		}
	}
}
