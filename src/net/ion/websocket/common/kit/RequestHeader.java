//	---------------------------------------------------------------------------
//	jWebSocket - RequestHeader Object
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

import java.util.Map;

import javolution.util.FastMap;

/**
 * Holds the header of the initial WebSocket request from the client to the
 * server. The RequestHeader internally maintains a FastMap to store key/values
 * pairs.
 * 
 * @author aschulze
 * @version $Id: RequestHeader.java,v 1.3 2011/07/15 07:14:12 bleujin Exp $
 */
public final class RequestHeader {

	private Map<String, Object> args = new FastMap<String, Object>();
	private static final String ARGS = "args";
	private static final String PROT = "prot";
	private static final String TIMEOUT = "timeout";

	/**
	 * Puts a new object value to the request header.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		args.put(key, value);
	}

	/**
	 * Returns the object value for the given key or {@code null} if the key
	 * does not exist in the header.
	 * 
	 * @param key
	 * @return object value for the given key or {@code null}.
	 */
	public Object get(String key) {
		return args.get(key);
	}

	/**
	 * Returns the string value for the given key or {@code null} if the key
	 * does not exist in the header.
	 * 
	 * @param key
	 * @return String value for the given key or {@code null}.
	 */
	public String getString(String key) {
		return (String) args.get(key);
	}

	/**
	 * Returns a FastMap of the optional URL arguments passed by the client.
	 * 
	 * @return FastMap of the optional URL arguments.
	 */
	public Map getArgs() {
		return (Map) args.get(ARGS);
	}

	/**
	 * Returns the sub protocol passed by the client or a default value if no
	 * sub protocol has been passed either in the header or in the URL
	 * arguments.
	 * 
	 * @param dftValue
	 * @return Sub protocol passed by the client or default value.
	 */
	public String getSubProtocol(String dftValue) {
		Map args = getArgs();
		String subprotocol = null;
		if (args != null) {
			subprotocol = (String) args.get(PROT);
		}
		return (subprotocol != null ? subprotocol : dftValue);
	}

	/**
	 * Returns the session timeout passed by the client or a default value if no
	 * session timeout has been passed either in the header or in the URL
	 * arguments.
	 * 
	 * @param dftTimeout
	 * @return Session timeout passed by the client or default value.
	 */
	public Integer getTimeout(Integer dftTimeout) {
		Map args = getArgs();
		Integer timeout = null;
		if (args != null) {
			try {
				timeout = Integer.parseInt((String) (args.get(TIMEOUT)));
			} catch (Exception ex) {
			}
		}
		return (timeout != null ? timeout : dftTimeout);
	}
}
