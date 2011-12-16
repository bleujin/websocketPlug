//	---------------------------------------------------------------------------
//	jWebSocket - TCP Connector
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
package net.ion.websocket.server.engine.tcp;

import org.apache.log4j.Logger;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.kit.RequestHeader;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import net.ion.websocket.common.kit.WebSocketProtocolAbstraction;

/**
 * Utility methods for tcp and nio engines.
 * 
 * @author jang
 */
public class EngineUtils {

	/**
	 * Validates draft header and constructs RequestHeader object.
	 */
	public static RequestHeader validateC2SRequest(Map responseMap, Logger logger) throws UnsupportedEncodingException {
		// Check for WebSocket protocol version.
		// If it is present and if it's something unrecognizable, force disconnect (return null).
		String draft = (String) responseMap.get(RequestHeader.WS_DRAFT);
		Integer version = (Integer) responseMap.get(RequestHeader.WS_VERSION);

		// run validation
		if (!WebSocketProtocolAbstraction.isValidDraft(draft)) {
			logger.error("Error in Handshake: Draft #'" + draft + "' not supported.");
			return null;
		}
		if (!WebSocketProtocolAbstraction.isValidVersion(version)) {
			logger.error("Error in Handshake: Version #'" + version + "' not supported.");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Client uses websocket protocol version #" + version + "/draft #" + draft + " for communication.");
		}

		RequestHeader header = new RequestHeader();
		Map<String, String> args = new HashMap<String, String>();
		String path = (String) responseMap.get("path");

		// isolate search string
		String searchString = "";
		if (path != null) {
			int pos = path.indexOf(CommonConstants.PATHARG_SEPARATOR);
			if (pos >= 0) {
				searchString = path.substring(pos + 1);
				if (searchString.length() > 0) {
					String[] lArgsArray = searchString.split(CommonConstants.ARGARG_SEPARATOR);
					for (int lIdx = 0; lIdx < lArgsArray.length; lIdx++) {
						String[] lKeyValuePair = lArgsArray[lIdx].split(CommonConstants.KEYVAL_SEPARATOR, 2);
						if (lKeyValuePair.length == 2) {
							args.put(lKeyValuePair[0], lKeyValuePair[1]);
							if (logger.isDebugEnabled()) {
								logger.debug("arg" + lIdx + ": " + lKeyValuePair[0] + "=" + lKeyValuePair[1]);
							}
						}
					}
				}
			}
		}

		// if no sub protocol given in request header , try
		String subProtocol = (String) responseMap.get(RequestHeader.WS_PROTOCOL);
		if (subProtocol == null) {
			subProtocol = args.get(RequestHeader.WS_PROTOCOL);
		}
		if (subProtocol == null) {
			subProtocol = CommonConstants.WS_SUBPROT_DEFAULT;
		}

		// Sub protocol header might contain multiple entries
		// (e.g. 'jwebsocket.org/json jwebsocket.org/xml chat.example.com/custom').
		// So, someone has to decide, which entry to use and send the client appropriate
		// choice. Right now, we will just choose the first one if more than one are
		// available.
		// TODO: implement subprotocol choice handling by deferring the decision to plugins/listeners
		if (subProtocol.indexOf(' ') != -1) {
			subProtocol = subProtocol.split(" ")[0];
			responseMap.put(RequestHeader.WS_PROTOCOL, subProtocol);
		}

		header.put(RequestHeader.WS_HOST, responseMap.get(RequestHeader.WS_HOST));
		header.put(RequestHeader.WS_ORIGIN, responseMap.get(RequestHeader.WS_ORIGIN));
		header.put(RequestHeader.WS_LOCATION, responseMap.get(RequestHeader.WS_LOCATION));
		header.put(RequestHeader.WS_PROTOCOL, subProtocol);
		header.put(RequestHeader.WS_PATH, responseMap.get(RequestHeader.WS_PATH));
		header.put(RequestHeader.WS_SEARCHSTRING, searchString);
		header.put(RequestHeader.URL_ARGS, args);
		header.put(RequestHeader.WS_DRAFT, draft == null ? CommonConstants.WS_DRAFT_DEFAULT : draft);
		header.put(RequestHeader.WS_VERSION, version == null ? CommonConstants.WS_VERSION_DEFAULT : version);

		return header;
	}
}
