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
package net.ion.websocket.server.engine;

import java.util.List;

import javolution.util.FastList;

import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.config.Config;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.config.ServerConstants;
import net.ion.websocket.common.kit.WebSocketRuntimeException;
import net.ion.websocket.server.engine.netty.NettyEngine;

/**
 * Class that represents the engine config
 *
 * @author puran
 * @version $Id: EngineConfig.java,v 1.1 2011/12/15 06:30:27 bleujin Exp $
 */
public final class EngineConfig implements Config, EngineConfiguration {

	private final String myId;
	private final String name;
	private final String jar;
	private final String context;
	private final String servlet;
	private final int sslPort;
	private final String keyStore;
	private final String keyStorePassword;
	private final int timeout;
	private final int maxframesize;
	private final List<String> domains;

	private int port;
	/**
	 * Constructor for engine
	 *
	 * @param id           the engine id
	 * @param name         the name of the engine
	 * @param jar          the jar file name
	 * @param port         the port number where engine runs
	 * @param timeout      the timeout value
	 * @param maxFrameSize the maximum frame size that engine will
	 *						receive without closing the connection
	 * @param domains      list of domain names
	 */
	public EngineConfig(String id, String name, String jar, int port, int sslPort, String keyStore, String keyStorePassword, String context, String servlet, int timeout, int maxFrameSize, List<String> domains) {
		this.myId = id;
		this.name = name;
		this.jar = jar;
		this.context = context;
		this.servlet = servlet;
		this.port = port;
		this.sslPort = sslPort;
		this.keyStore = keyStore;
		this.keyStorePassword = keyStorePassword;
		this.timeout = timeout;
		this.maxframesize = maxFrameSize;
		this.domains = domains;
		validate();
	}

	
	public final static EngineConfig test(int port){
		List<String> domains = new FastList<String>();
		domains.add("http://i-on.net");
		
		return new EngineConfig("netty" + port, 
				NettyEngine.class.getCanonicalName(), 
				"-", 
				port, 
				CommonConstants.DEFAULT_SSLPORT, 
				ServerConstants.WEBSOCKET_KEYSTORE, 
				ServerConstants.WEBSOCKET_KS_DEF_PWD, 
				CommonConstants.WEBSOCKET_DEF_CONTEXT, 
				CommonConstants.WEBSOCKET_DEF_SERVLET, 
				CommonConstants.DEFAULT_TIMEOUT, 
				CommonConstants.DEFAULT_MAX_FRAME_SIZE, domains) ;		
	}
	
	
	
	
	
	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return myId;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return the jar
	 */
	@Override
	public String getJar() {
		return jar;
	}

	/**
	 * @return the port
	 */
	@Override
	public int getPort() {
		return port;
	}

	/**
	 * @return the SSL port
	 */
	@Override
	public int getSSLPort() {
		return sslPort;
	}

	/**
	 * Returns the context for servlet based engines like Jetty
	 * @return the context for servlet based engines, null for native servers
	 */
	@Override
	public String getContext() {
		return context;
	}

	/**
	 * Returns the servlet for servlet based engines like Jetty
	 * @return the servlet for servlet based engines, null for native servers
	 */
	@Override
	public String getServlet() {
		return servlet;
	}

	/**
	 * @return the timeout
	 */
	@Override
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @return the max frame size
	 */
	@Override
	public int getMaxFramesize() {
		return maxframesize;
	}

	/**
	 * @return the domains
	 */
	@Override
	public List<String> getDomains() {
		return domains;
	}

	/**
	 * validate the engine configuration
	 *
	 * @throws WebSocketRuntimeException if any of the engine configuration is mising
	 */
	@Override
	public void validate() {
		if ((myId != null && myId.length() > 0)
				&& (name != null && name.length() > 0)
				&& (jar != null && jar.length() > 0)
				&& (domains != null && domains.size() > 0)
				// leaving port empty needs to be allowed eg. for Jetty
				// when using underlying WebSocket Servlets
				&& (port >= 0 && port < 65536)
				&& (sslPort >= 0 && sslPort < 65536)
				&& keyStore != null && keyStore.length() > 0
				&& keyStorePassword != null && keyStorePassword.length() > 0
				&& timeout >= 0) {
			return;
		}
		throw new WebSocketRuntimeException("Missing one of the engine configuration, please check your configuration file");
	}

	/**
	 * @return the KeyStore
	 */
	public String getKeyStore() {
		return keyStore;
	}

	/**
	 * @return the KeyStorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}


	@Override
	public void setPort(int port) {
		this.port = port ;
	}
}
