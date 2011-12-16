//	---------------------------------------------------------------------------
//	jWebSocket - Server Configuration Constants
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

/**
 * Provides a global shared container for the jWebSocket configuration settings.
 *
 * @author aschulze
 * @version $Id: WebSocketServerConstants.java,v 1.1 2011/12/15 06:30:22 bleujin Exp $
 */
public final class ServerConstants {


	/**
	 * Current version string of the jWebSocket package.
	 */
	public static final String VERSION_STR = "1.0b1 (build 10902)";
	/**
	 * Name space base for tokens and plug-ins.
	 */
	public static final String NS_BASE = "org.jwebsocket";
	/**
	 * constant for default installation
	 */
	public static final String DEFAULT_INSTALLATION = "prod";
	/**
	 * Constant for JWEBSOCKET_HOME environment variable
	 */
	public static final String WEBSOCKET_HOME = "JWEBSOCKET_HOME";
	/**
	 * Constant for CATALINA_HOME
	 */
	public static final String CATALINA_HOME = "CATALINA_HOME";
	/**
	 * Constant for jWebSocket.xml configuration file
	 */
	public static final String JWEBSOCKET_XML = "jWebSocket.xml";
	/**
	 * Constant for jWebSocket.ks key store file
	 */
	public static final String WEBSOCKET_KEYSTORE = "jWebSocket.ks";
	/**
	 * Default password for demo keystore
	 */
	public static final String WEBSOCKET_KS_DEF_PWD = "jWebSocket";
	/**
	 * Constant for jWebSocket.xml configuration file
	 */
	public static final String JWEBSOCKET_OVERRIDE_XML = "jWebSocket-override.xml";

	/**
	 * Default engine for jWebSocket server.
	 */
	public static String DEFAULT_ENGINE = "tcp";
	/**
	 * Default node id if non given in jWebSocket.xml.
	 * Empty means do not use node-id for single stand-alone systems
	 */
	public static String DEFAULT_NODE_ID = "";

}
