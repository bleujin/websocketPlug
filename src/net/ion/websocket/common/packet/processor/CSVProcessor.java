//	---------------------------------------------------------------------------
//	jWebSocket - CSV Token Processor
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
package net.ion.websocket.common.packet.processor;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;

import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.RawPacket;
import net.ion.websocket.common.token.Token;
import net.ion.websocket.common.token.TokenFactory;

/**
 * converts CSV formatted data packets into tokens and vice versa.
 * 
 * @author aschulze
 */
public class CSVProcessor {

	// TODO: Logging cannot be used in common module because not supported on all clients
	// private static Logger log = Logging.getLogger(CSVProcessor.class);
	/**
	 * converts a CSV formatted data packet into a token.
	 * 
	 * @param dataPacket
	 * @return
	 */
	public static Token packetToToken(WebSocketPacket dataPacket) {
		Token token = TokenFactory.createToken();
		try {
			String data = dataPacket.getString("UTF-8");
			String[] items = data.split(",");
			for (int i = 0; i < items.length; i++) {
				String[] keyVal = items[i].split("=", 2);
				if (keyVal.length == 2) {
					String val = keyVal[1];
					if (val.length() <= 0) {
						token.setValidated(keyVal[0], null);
					} else if (val.startsWith("\"") && val.endsWith("\"")) {
						// unescape commata by \x2C
						val = val.replace("\\x2C", ",");
						// unescape quotes by \x22
						val = val.replace("\\x22", "\"");
						token.setValidated(keyVal[0], val.substring(1, val.length() - 1));
					} else {
						token.setValidated(keyVal[0], val);
					}
				}
			}
		} catch (UnsupportedEncodingException ignore) {
			ignore.printStackTrace() ;
		}
		return token;
	}

	private static String stringToCSV(String string) {
		// escape commata by \x2C
		string = string.replace(",", "\\x2C");
		// escape quotes by \x22
		string = string.replace("\"", "\\x22");
		return ("\"" + string + "\"");
	}

	private static String collectionToCSV(Collection<Object> collection) {
		String result = "";
		for (Object item : collection) {
			String res = objectToCSV(item);
			result += res + "|";
		}
		if (result.length() > 1) {
			result = result.substring(0, result.length() - 1);
		}
		result = "[" + result + "]";
		return result;
	}

	private static String objectToCSV(Object obj) {
		String result;
		if (obj == null) {
			result = "null";
		} else if (obj instanceof String) {
			result = stringToCSV((String) obj);
		} else if (obj instanceof Collection) {
			result = collectionToCSV((Collection<Object>) obj);
		} else {
			result = "\"" + obj.toString() + "\"";
		}
		return result;
	}

	/**
	 * converts a token into a CSV formatted data packet.
	 * 
	 * @param token
	 * @return
	 */
	public static WebSocketPacket tokenToPacket(Token token) {
		String data = "";
		Iterator<String> iter = token.getKeyIterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = token.getString(key);
			data += key + "=" + objectToCSV(value) + (iter.hasNext() ? "," : "");
		}
		WebSocketPacket packet = null;
		try {
			packet = new RawPacket(data, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
			ignore.printStackTrace() ;
		}
		return packet;
	}
}
