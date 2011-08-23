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
package net.ion.websocket.common.config;

import static net.ion.websocket.common.config.CommonConstants.CATALINA_HOME;
import static net.ion.websocket.common.config.CommonConstants.DEFAULT_INSTALLATION;
import static net.ion.websocket.common.config.CommonConstants.DEFAULT_PROTOCOL;
import static net.ion.websocket.common.config.CommonConstants.JWEBSOCKET_HOME;
import static net.ion.websocket.common.config.CommonConstants.JWEBSOCKET_XML;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import net.ion.websocket.common.config.xml.EngineConfig;
import net.ion.websocket.common.config.xml.FilterConfig;
import net.ion.websocket.common.config.xml.PluginConfig;
import net.ion.websocket.common.config.xml.RightConfig;
import net.ion.websocket.common.config.xml.RoleConfig;
import net.ion.websocket.common.config.xml.ServerConfig;
import net.ion.websocket.common.config.xml.UserConfig;
import net.ion.websocket.common.kit.WebSocketRuntimeException;
import net.ion.websocket.common.logging.Logging;

import org.apache.log4j.Logger;

/**
 * Represents the jWebSocket configuration. This class is immutable and should
 * not be overridden.
 *
 * @author puran
 * @version $Id: SocketConfig.java,v 1.2 2011/07/23 04:35:53 bleujin Exp $
 */
public final class SocketConfig implements Config {

	// DON'T SET LOGGER HERE! NEEDS TO BE INITIALIZED FIRST!
	private static Logger mLog = null;

	private final String mInstallation;
	private final String mProtocol;
	private final String jWebSocketHome;
	private final String mLibraryFolder;
	private final String mInitializer;
	private final List<EngineConfig> mEngines;
	private final List<ServerConfig> mServers;
	private final List<UserConfig> mUsers;
	private final List<PluginConfig> mPlugins;
	private final List<FilterConfig> mFilters;
	private final LoggingConfig mLoggingConfig;
	private final List<RightConfig> mGlobalRights;
	private final List<RoleConfig> mGlobalRoles;
	private static SocketConfig mConfig = null;

	/**
	 * @return the installation
	 */
	public String getInstallation() {
		if (mInstallation == null || mInstallation.length() == 0) {
			return DEFAULT_INSTALLATION;
		}
		return mInstallation;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		if (mProtocol == null || mProtocol.length() == 0) {
			return DEFAULT_PROTOCOL;
		}
		return mProtocol;
	}

	/**
	 * @return the jWebSocketHome
	 */
	public String getjWebSocketHome() {
		return jWebSocketHome;
	}

	/**
	 * @return the libraryFolder
	 */
	public String getLibraryFolder() {
		return mLibraryFolder;
	}

	/**
	 * @return the initializer
	 */
	public String getInitializer() {
		return mInitializer;
	}

	/**
	 * @return the config
	 */
	public static SocketConfig getConfig() {
		return mConfig;
	}

	/**
	 * private constructor used by the builder
	 */
	private SocketConfig(Builder builder) {
		if (builder.engines == null || builder.servers == null
				|| builder.users == null || builder.globalRights == null
				|| builder.globalRoles == null || builder.filters == null
				|| builder.loggingConfig == null) {
			throw new WebSocketRuntimeException(
					"Configuration is not loaded completely.");
		}
		mInstallation = builder.installation;
		mProtocol = builder.protocol;
		jWebSocketHome = builder.jWebSocketHome;
		mLibraryFolder = builder.libraryFolder;
		mInitializer = builder.initializer;
		mEngines = builder.engines;
		mServers = builder.servers;
		mUsers = builder.users;
		mPlugins = builder.plugins;
		mFilters = builder.filters;
		mLoggingConfig = builder.loggingConfig;
		mGlobalRights = builder.globalRights;
		mGlobalRoles = builder.globalRoles;
		// validate the config
		validate();
	}

	/**
	 * Config builder class.
	 *
	 * @author puran
	 * @version $Id: SocketConfig.java,v 1.2 2011/07/23 04:35:53 bleujin Exp $
	 */
	public static class Builder {

		private String installation;
		private String protocol;
		private String jWebSocketHome;
		private String libraryFolder;
		private String initializer;
		private List<EngineConfig> engines;
		private List<ServerConfig> servers;
		private List<UserConfig> users;
		private List<PluginConfig> plugins;
		private List<FilterConfig> filters;
		private LoggingConfig loggingConfig;
		private List<RightConfig> globalRights;
		private List<RoleConfig> globalRoles;

		public Builder addInstallation(String theInstallation) {
			installation = theInstallation;
			return this;
		}

		public Builder addProtocol(String theProtocol) {
			protocol = theProtocol;
			return this;
		}

		public Builder addJWebSocketHome(String theJWebSocketHome) {
			jWebSocketHome = theJWebSocketHome;
			return this;
		}

		public Builder addInitializer(String theInitializer) {
			initializer = theInitializer;
			return this;
		}

		public Builder addLibraryFolder(String theLibraryFolder) {
			libraryFolder = theLibraryFolder;
			return this;
		}

		public Builder addEngines(List<EngineConfig> theEngines) {
			engines = theEngines;
			return this;
		}

		public Builder addServers(List<ServerConfig> theServers) {
			servers = theServers;
			return this;
		}

		public Builder addPlugins(List<PluginConfig> thePlugins) {
			plugins = thePlugins;
			return this;
		}

		public Builder addFilters(List<FilterConfig> theFilters) {
			filters = theFilters;
			return this;
		}

		public Builder addLoggingConfig(List<LoggingConfig> theLoggingConfigs) {
			loggingConfig = theLoggingConfigs.get(0);
			return this;
		}

		public Builder addGlobalRights(List<RightConfig> theRights) {
			globalRights = theRights;
			return this;
		}

		public Builder addGlobalRoles(List<RoleConfig> theRoles) {
			globalRoles = theRoles;
			return this;
		}

		public Builder addUsers(List<UserConfig> theUsers) {
			users = theUsers;
			return this;
		}

		public synchronized SocketConfig buildConfig() {
			if (mConfig == null) {
				mConfig = new SocketConfig(this);
			}
			return mConfig;
		}
	}

	/**
	 * @return the engines
	 */
	public List<EngineConfig> getEngines() {
		return Collections.unmodifiableList(mEngines);
	}

	/**
	 * @return the servers
	 */
	public List<ServerConfig> getServers() {
		return Collections.unmodifiableList(mServers);
	}

	/**
	 * @return the users
	 */
	public List<UserConfig> getUsers() {
		return Collections.unmodifiableList(mUsers);
	}

	/**
	 * @return the plugins
	 */
	public List<PluginConfig> getPlugins() {
		return Collections.unmodifiableList(mPlugins);
	}

	/**
	 * @return the filters
	 */
	public List<FilterConfig> getFilters() {
		return Collections.unmodifiableList(mFilters);
	}

	/**
	 * @return the logging config object
	 */
	public LoggingConfig getLoggingConfig() {
		return mLoggingConfig;
	}

	/**
	 * @return the globalRights
	 */
	public List<RightConfig> getGlobalRights() {
		return Collections.unmodifiableList(mGlobalRights);
	}

	/**
	 * @return the globalRoles
	 */
	public List<RoleConfig> getGlobalRoles() {
		return Collections.unmodifiableList(mGlobalRoles);
	}

	/**
	 * {@inheritDoc}
	 */
	public void validate() {
		if ((mEngines == null || mEngines.isEmpty())
				|| (mServers == null || mServers.isEmpty())
				|| (mUsers == null || mUsers.isEmpty())
				|| (mPlugins == null || mPlugins.isEmpty())
				|| (mFilters == null || mFilters.isEmpty())
				|| (mLoggingConfig == null)
				|| (mGlobalRights == null || mGlobalRights.isEmpty())
				|| (mGlobalRoles == null || mGlobalRoles.isEmpty())) {
			throw new WebSocketRuntimeException(
					"Missing one of the server configuration, please check your configuration file");
		}
	}

	private static void checkLogs() {
		if (mLog == null) {
			mLog = Logging.getLogger(SocketConfig.class);
		}
	}

	/**
	 * private method that checks the path of the jWebSocket.xml file
	 *
	 * @return the path to jWebSocket.xml
	 */
	public static String getConfigurationPath() {
		String webSocketXML = null;
		String webSocketHome = null;
		String lFileSep = System.getProperty("file.separator");
		File configFile;

		// try to obtain JWEBSOCKET_HOME environment variable
		webSocketHome = System.getenv(JWEBSOCKET_HOME);
		if (webSocketHome != null) {
			// append trailing slash if needed
			if (!webSocketHome.endsWith(lFileSep)) {
				webSocketHome += lFileSep;
			}
			// jWebSocket.xml can be located in %JWEBSOCKET_HOME%/conf
			webSocketXML = webSocketHome + "conf" + lFileSep + JWEBSOCKET_XML;
			configFile = new File(webSocketXML);
			if (configFile.exists()) {
				return webSocketXML;
			}
		}

		// try to obtain CATALINA_HOME environment variable
		webSocketHome = System.getenv(CATALINA_HOME);
		if (webSocketHome != null) {
			// append trailing slash if needed
			if (!webSocketHome.endsWith(lFileSep)) {
				webSocketHome += lFileSep;
			}
			// jWebSocket.xml can be located in %CATALINA_HOME%/conf
			webSocketXML = webSocketHome + "conf" + lFileSep + JWEBSOCKET_XML;
			configFile = new File(webSocketXML);
			if (configFile.exists()) {
				return webSocketXML;
			}
		}

		// finally try to find config file at %CLASSPATH%/conf/
		URL lURL = Thread.currentThread().getContextClassLoader().getResource("conf/" + JWEBSOCKET_XML);
		if (lURL != null) {
/*
			lWebSocketXML = lURL.getFile();
			System.out.println("WebSocketXML - Filename: " + lWebSocketXML);
			lFile = new File(lWebSocketXML);
			if (lFile.exists()) {
				return lWebSocketXML;
			}
*/
			try {
				URI lFilename = lURL.toURI();
				// System.out.println("URI Filename: " + lFilename);
				configFile = new File(lFilename);
				if (configFile.exists()) {
					webSocketXML = configFile.getPath();
					return webSocketXML;
				}
			} catch (Exception ex) {
				//TODO: log exception
				// System.out.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
		}
		
		configFile = new File("shared/J2SE/jWebSocketServerAPI/org/jwebsocket/config/jWebSocket.xml") ;
		

		return configFile.getPath()	;
	}

	/**
	 * private method that checks the path of the jWebSocket.xml file
	 *
	 * @return the path to jWebSocket.xml
	 */
	public static String getLibraryFolderPath(String fileName) {
		String lWebSocketLib = null;
		String lWebSocketHome = null;
		String lFileSep = null;
		File lFile = null;

		checkLogs();
		
		// try to load lib from %JWEBSOCKET_HOME%/libs folder
		lWebSocketHome = System.getenv(JWEBSOCKET_HOME);
		lFileSep = System.getProperty("file.separator");
		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// jar can to be located in %JWEBSOCKET_HOME%/libs
			lWebSocketLib = lWebSocketHome + "libs" + lFileSep + fileName;
			lFile = new File(lWebSocketLib);
			if (lFile.exists()) {
				if (mLog.isDebugEnabled()) {
					mLog.debug("Found lib at " + lWebSocketLib + "...");
				}
				return lWebSocketLib;
			} else {
				if (mLog.isDebugEnabled()) {
					mLog.debug(fileName + " not found at %" + JWEBSOCKET_HOME + "%/libs.");
				}
			}
		}

		// try to load lib from %CATALINA_HOME%/libs folder
		lWebSocketHome = System.getenv(CATALINA_HOME);
		lFileSep = System.getProperty("file.separator");
		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// jars can to be located in %CATALINA_HOME%/lib
			lWebSocketLib = lWebSocketHome + "lib" + lFileSep + fileName;
			lFile = new File(lWebSocketLib);
			if (lFile.exists()) {
				if (mLog.isDebugEnabled()) {
					mLog.debug("Found lib at " + lWebSocketLib + "...");
				}
				return lWebSocketLib;
			} else {
				if (mLog.isDebugEnabled()) {
					mLog.debug(fileName + " not found at %" + CATALINA_HOME + "/lib%.");
				}
			}
		}

		return null;
	}
}
