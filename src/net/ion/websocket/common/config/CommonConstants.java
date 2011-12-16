//	---------------------------------------------------------------------------
//	jWebSocket - Common Configuration Constants
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
package net.ion.websocket.common.config;

import net.ion.websocket.common.kit.*;

import java.util.List;

import javolution.util.FastList;

/**
 *
 * @author aschulze
 */
public class CommonConstants {

	/**
	 * jWebSocket copyright string - MAY NOT BE CHANGED due to GNU LGPL v3.0 license!
	 * Please ask for conditions of a commercial license on demand.
	 */
	public static final String COPYRIGHT = "(c) 2010, 2011 i-on (i-on.net), Korea (KR), bleujin";
	/**
	 * jWebSocket license string - MAY NOT BE CHANGED due to GNU LGPL v3.0 license!
	 * Please ask for conditions of a commercial license on demand.
	 */
	public static final String LICENSE = "Distributed under GNU LGPL License Version 3.0 (http://www.gnu.org/licenses/lgpl.html)";
	/**
	 * jWebSocket vendor string - MAY NOT BE CHANGED due to GNU LGPL v3.0 license!
	 * Please ask for conditions of a commercial license on demand.
	 */
	public static final String VENDOR = "i-on.net";
	/**
	 * jWebSocket sub protocol prefix
	 */
	public final static String WS_SUBPROT_PREFIX = "net.ion";
	/**
	 * jWebSocket JSON sub protocol
	 */
	public final static String WS_SUBPROT_JSON = WS_SUBPROT_PREFIX + ".json";
	/**
	 * jWebSocket CSV sub protocol
	 */
	public final static String WS_SUBPROT_CSV = WS_SUBPROT_PREFIX + ".csv";
	/**
	 * jWebSocket XML sub protocol
	 */
	public final static String WS_SUBPROT_XML = WS_SUBPROT_PREFIX + ".xml";
	/**
	 * jWebSocket custom specific text sub protocol
	 */
	public final static String WS_SUBPROT_TEXT = WS_SUBPROT_PREFIX + ".text";
	/**
	 * jWebSocket custom specific binary sub protocol
	 */
	public final static String WS_SUBPROT_BINARY = WS_SUBPROT_PREFIX + ".binary";
	/**
	 * Default protocol
	 */
	public static String WS_SUBPROT_DEFAULT = WS_SUBPROT_JSON;
	/**
	 * JSON sub protocol format
	 */
	public final static String WS_FORMAT_JSON = "json";
	/**
	 * CSV sub protocol format
	 */
	public final static String WS_FORMAT_CSV = "csv";
	/**
	 * XML sub protocol format
	 */
	public final static String WS_FORMAT_XML = "xml";
	/**
	 * Binary sub protocol format
	 */
	public final static String WS_FORMAT_BINARY = "binary";
	/**
	 * Custom specific sub protocol format
	 */
	public final static String WS_FORMAT_TEXT = "text";
	/**
	 * Default sub protocol format
	 */
	public static String WS_FORMAT_DEFAULT = WS_FORMAT_JSON;
	/**
	 * WebSocket protocol hybi draft 10 (http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-76)
	 */
	public final static String WS_HIXIE_DRAFT_76 = "76";
	/**
	 * WebSocket protocol hybi draft 03 (http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-02)
	 */
	public final static String WS_HYBI_DRAFT_02 = "2";
	/**
	 * WebSocket protocol hybi draft 03 (http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-03)
	 */
	public final static String WS_HYBI_DRAFT_03 = "3";
	/**
	 * WebSocket protocol hybi draft 07 (http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-07)
	 */
	public final static String WS_HYBI_DRAFT_07 = "7";
	/**
	 * WebSocket protocol hybi draft 08 (http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-08)
	 */
	public final static String WS_HYBI_DRAFT_08 = "8";
	/**
	 * WebSocket protocol hybi draft 10 (http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-10)
	 */
	public final static String WS_HYBI_DRAFT_10 = "10";
	/**
	 * WebSocket earliest supported hixie version
	 */
	public final static int WS_EARLIEST_SUPPORTED_HIXIE_VERSION = 75;
	/**
	 * WebSocket latest supported hixie version
	 */
	public final static int WS_LATEST_SUPPORTED_HIXIE_VERSION = 76;
	/**
	 * WebSocket latest supported hybi version
	 */
	public final static int WS_LATEST_SUPPORTED_HYBI_VERSION = 8;
	/**
	 * WebSocket earliest supported hixie draft
	 */
	public final static String WS_EARLIEST_SUPPORTED_HIXIE_DRAFT = "75";
	/**
	 * WebSocket latest supported hixie draft
	 */
	public final static String WS_LATEST_SUPPORTED_HIXIE_DRAFT = "76";
	/**
	 * WebSocket latest supported hybi draft
	 */
	public final static String WS_LATEST_SUPPORTED_HYBI_DRAFT = "10";
	/**
	 * WebSocket supported hixie versions
	 */
	public final static List<Integer> WS_SUPPORTED_HIXIE_VERSIONS = new FastList<Integer>();
	/**
	 * WebSocket supported hixie drafts
	 */
	public final static List<String> WS_SUPPORTED_HIXIE_DRAFTS = new FastList<String>();
	/**
	 * WebSocket supported hybi versions
	 */
	public final static List<Integer> WS_SUPPORTED_HYBI_VERSIONS = new FastList<Integer>();
	/**
	 * WebSocket supported hybi drafts
	 */
	public final static List<String> WS_SUPPORTED_HYBI_DRAFTS = new FastList<String>();
	/**
	 * WebSocket default protocol version
	 */
	public final static int WS_VERSION_DEFAULT = 8;
	/**
	 * WebSocket default protocol version
	 */
	public final static int WS_DRAFT_DEFAULT = 10;
	/**
	 * Use text format as default encoding for WebSocket Packets if not explicitly specified
	 */
	public final static WebSocketEncoding WS_ENCODING_DEFAULT = WebSocketEncoding.TEXT;
	/**
	 * Separator between the path and the argument list in the URL.
	 */
	public static String PATHARG_SEPARATOR = ";";
	/**
	 * Separator between the various URL arguments.
	 */
	public static String ARGARG_SEPARATOR = ",";
	/**
	 * Separator between the key and the value of each URL argument.
	 */
	public static String KEYVAL_SEPARATOR = "=";
	/**
	 * Minimum allow outgoing TCP Socket port.
	 */
	public static int MIN_IN_PORT = 1024;
	/**
	 * Maximum allow outgoing TCP Socket port.
	 */
	public static int MAX_IN_PORT = 65535;
	/**
	 * the default maximum frame size if not configured
	 */
	public static final int DEFAULT_MAX_FRAME_SIZE = 16384;
	/**
	 * Default socket port for jWebSocket clients.
	 */
	public static int DEFAULT_PORT = 8787;
	/**
	 * Default socket port for jWebSocket clients.
	 */
	public static int DEFAULT_SSLPORT = 9797;
	/**
	 * Default context on app servers and servlet containers
	 */
	public static final String WEBSOCKET_DEF_CONTEXT = "/webSocket";
	/**
	 * Default servlet on app servers and servlet containers
	 */
	public static final String WEBSOCKET_DEF_SERVLET = "/webSocket";
	/**
	 * Default Session Timeout for client connections (120000ms = 2min)
	 */
	public static int DEFAULT_TIMEOUT = 180000;
	/**
	 * private scope, only authenticated user can read and write his personal items
	 */
	public static final String SCOPE_PRIVATE = "private";
	/**
	 * public scope, everybody can read and write items from this scope
	 */
	public static final String SCOPE_PUBLIC = "public";

	static {
		WS_SUPPORTED_HIXIE_VERSIONS.add(75);
		WS_SUPPORTED_HIXIE_VERSIONS.add(76);

		WS_SUPPORTED_HYBI_VERSIONS.add(7);
		WS_SUPPORTED_HYBI_VERSIONS.add(8);

		WS_SUPPORTED_HIXIE_DRAFTS.add("75");
		WS_SUPPORTED_HIXIE_DRAFTS.add("76");

		WS_SUPPORTED_HYBI_DRAFTS.add("7");
		WS_SUPPORTED_HYBI_DRAFTS.add("8");
		WS_SUPPORTED_HYBI_DRAFTS.add("9");
		WS_SUPPORTED_HYBI_DRAFTS.add("10");
	}

	public static String XUSER_INFO = "XSOCKET-USER-INFO" ;
}
