//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
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
package net.ion.websocket.common.config.xml;

import net.ion.websocket.common.api.ServerConfiguration;
import net.ion.websocket.common.config.Config;
import net.ion.websocket.common.kit.WebSocketRuntimeException;

/**
 * Represents the server config
 * 
 * @author puran
 * @version $Id: ServerConfig.java,v 1.2 2011/07/23 04:35:54 bleujin Exp $
 * 
 */
public final class ServerConfig implements Config, ServerConfiguration {

	private final String id;
	private final String name;
	private final String jar;

	public ServerConfig(String id, String name, String jar) {
		this.id = id;
		this.name = name;
		this.jar = jar;
		// validate the server configuration
		validate();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the jar
	 */
	public String getJar() {
		return jar;
	}

	/**
	 * {@inheritDoc}
	 */
	public void validate() {
		if ((id != null && id.length() > 0) && (name != null && name.length() > 0) && (jar != null && jar.length() > 0)) {
			return;
		}
		throw new WebSocketRuntimeException("Missing one of the server configuration, please check your configuration file");
	}

	public String getURIPath() {
		return "";
	}
}
