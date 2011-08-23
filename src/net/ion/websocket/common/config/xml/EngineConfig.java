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

import java.util.List;

import net.ion.websocket.common.api.EngineConfiguration;
import net.ion.websocket.common.config.Config;
import net.ion.websocket.common.kit.WebSocketRuntimeException;


/**
 * Class that represents the engine config
 * 
 * @author puran
 * @version $Id: EngineConfig.java,v 1.2 2011/07/23 04:35:54 bleujin Exp $
 */
public final class EngineConfig implements Config, EngineConfiguration {

	private final String id;
	private final String name;
	private final String jar;
	private final int port;
	private final int timeout;
	private final int maxframesize;
	private final List<String> domains;

	/**
	 * Constructor for engine
	 * 
	 * @param id
	 *            the engine id
	 * @param name
	 *            the name of the engine
	 * @param jar
	 *            the jar file name
	 * @param port
	 *            the port number where engine runs
	 * @param timeout
	 *            the timeout value
	 * @param maxFrameSize
	 *            the maximum frame size that engine will receive without
	 *            closing the connection
	 * @param domains
	 *            list of domain names
	 */
	public EngineConfig(String id, String name, String jar, int port, int timeout, int maxFrameSize, List<String> domains) {
		this.id = id;
		this.name = name;
		this.jar = jar;
		this.port = port;
		this.timeout = timeout;
		this.maxframesize = maxFrameSize;
		this.domains = domains;
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
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @return the max frame size
	 */
	public int getMaxFramesize() {
		return maxframesize;
	}

	/**
	 * @return the domains
	 */
	public List<String> getDomains() {
		return domains;
	}

	/**
	 * validate the engine configuration
	 * 
	 * @throws WebSocketRuntimeException
	 *             if any of the engine configuration is mising
	 */
	public void validate() {
		if ((id != null && id.length() > 0) && (name != null && name.length() > 0) && (jar != null && jar.length() > 0) && (domains != null && domains.size() > 0) && port >= 1024 && timeout >= 0) {
			return;
		}
		throw new WebSocketRuntimeException("Missing one of the engine configuration, " + "please check your configuration file");
	}
	
}
