package net.ion.websocket.common.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.common.api.EngineConfiguration;
import net.ion.websocket.common.api.ServerConfiguration;
import net.ion.websocket.server.context.ContextLoader;
import net.ion.websocket.server.context.ServiceContext;

import org.apache.commons.configuration.ConfigurationException;

public class ServerConfigParser {

	private XMLConfig root ;
	private DefaultServerConfig serverConfig ;
	private DefaultEngineConfig engineConfig ;
	private ServiceContext context ;
	
	private List<FilterInfo> filters = ListUtil.newList() ;
	private List<PlugInInfo> plugins = ListUtil.newList() ;
	private List<ListenerInfo> listeners = ListUtil.newList() ;
	private AradonConfiguration aradonConfig ;
	
	private String baseDir ;
	private ServerConfigParser(XMLConfig root, String baseDir) {
		this.root = root ;
		this.baseDir = baseDir ;
	}

	public final static ServerConfigParser parse(XMLConfig root) throws ConfigurationException, InstanceCreationException{
		return parse(root, "./") ;
	}
	public final static ServerConfigParser parse(XMLConfig root, String baseDir) throws ConfigurationException, InstanceCreationException{
		ServerConfigParser result = new ServerConfigParser(root, baseDir);
		result.init() ;
		return result ;
	}
	
	private void init() throws ConfigurationException, InstanceCreationException {
		this.serverConfig = new DefaultServerConfig(root.firstChild("server")) ;
		this.engineConfig = new DefaultEngineConfig(root.firstChild("engine")) ;
		this.context = loadContext(root.firstChild("context")) ;
		
		this.plugins = makePlugInInfos(root.firstChild("plugins")) ;
		this.listeners = makeListenerInfos(root.firstChild("listeners")) ;
		this.aradonConfig = AradonConfiguration.create(this.baseDir, root.firstChild("aradon")) ;
		
	}

	private List<ListenerInfo> makeListenerInfos(XMLConfig listeners) throws ConfigurationException, InstanceCreationException {
		List<XMLConfig> pluginConfig =  listeners.children("listener") ;
		
		List<ListenerInfo> result = ListUtil.newList() ;
		for (XMLConfig config : pluginConfig) {
			result.add(ListenerInfo.create(config)) ;
		}

		return result ;
	}

	private List<PlugInInfo> makePlugInInfos(XMLConfig plugins) throws ConfigurationException, InstanceCreationException {
		List<XMLConfig> pluginConfig =  plugins.children("plugin") ;
		
		List<PlugInInfo> result = ListUtil.newList() ;
		for (XMLConfig config : pluginConfig) {
			result.add(PlugInInfo.create(config)) ;
		}

		return result ;
	}

	private ServiceContext loadContext(XMLConfig contextConfig) throws ConfigurationException, InstanceCreationException {
		return ContextLoader.load(contextConfig) ;
	}

	public List<PlugInInfo> getPlugins(){
		return Collections.unmodifiableList(plugins) ;
	}

	public ServerConfiguration getServerConfiguration(){
		return serverConfig ;
	}

	public ServerConfiguration getServerConfiguration(String id){
		if (StringUtil.isAlphanumericUnderbar(id) && StringUtil.isNotBlank(id)) serverConfig.setId(id) ;
		return serverConfig ;
	}
	
	public EngineConfiguration getEngineConfiguration(int defaultPort){
		if (defaultPort >= 1024) engineConfig.setPort(defaultPort) ;
		return engineConfig ;
	}

	public AradonConfiguration getAradonConfiguration(){
		return aradonConfig ;
	}
	
	public ServiceContext getContext() {
		return context ;
	}

	public List<ListenerInfo> getListener() {
		return listeners ;
	}
	public List<FilterInfo> getFilter() {
		return filters ;
	}
	
}


class DefaultServerConfig implements ServerConfiguration {

	private XMLConfig config ;
	private String sid ;
	public DefaultServerConfig(XMLConfig config) {
		this.config = config ;
		this.sid = config.getString("[@id]", "ts0");
	}
	
	public String getJar() {
		return null;
	}

	void setId(String sid){
		this.sid = sid ;
	}
	
	public String getId() {
		return sid;
	}

	public String getName() {
		return config.getString("[@name]", "SererName");
	}

	public String getURIPath() {
		return config.getString("[@uripath]", "");
	}
	
}


class DefaultEngineConfig implements EngineConfiguration {

	private XMLConfig config ;
	private int port ;
	
	DefaultEngineConfig(XMLConfig config) {
		this.config = config ;
		this.port = config.getInt("[@port]", 8080);
	}
	
	public List<String> getDomains() {
		String value = config.getString("domain", "localhost") ;
		return Arrays.asList(StringUtil.split(value, ",")) ;
	}

	public String getJar() {
		return null;
	}

	public int getMaxFramesize() {
		return config.getInt("[@max-frame-size]", 16384);
	}

	public int getPort() {
		return port;
	}
	
	void setPort(int port){
		this.port = port ;
	}

	public int getTimeout() {
		return config.getInt("[@timeout]", 120000);
	}

	public String getId() {
		return config.getString("id", "netty0");
	}

	public String getName() {
		return config.getString("name", "Netty");
	}
	
}
