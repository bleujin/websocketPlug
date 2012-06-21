package net.ion.chat.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.api.ChatListener;
import net.ion.chat.api.ChatPlugIn;
import net.ion.chat.api.ChatConstants;
import net.ion.chat.api.ChatHandler;
import net.ion.chat.config.EngineConfiguration;
import net.ion.chat.exception.ChatRuntimeException;
import net.ion.chat.impl.listener.ChatEvent;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.ChatServer;
import net.ion.chat.server.UserBean;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.chat.util.NormalMessagePacket;
import net.ion.chat.util.ChatMessage;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.WebServer;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;
import net.ion.nradon.handler.HttpToWebSocketHandler;
import net.ion.nradon.handler.StaticFileHandler;
import net.ion.nradon.handler.aradon.AradonHandler;
import net.ion.nradon.handler.event.ServerEventHandler;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.nradon.handler.logging.LoggingHandler;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.XMLConfig;

import org.apache.commons.configuration.ConfigurationException;

public class ChatEngine implements WebSocketHandler, ServerEventHandler, ChatHandler {

	private final Aradon aradon;
	private EngineConfiguration econfig ;
	private List<ChatPlugIn> plugins = ListUtil.newList();
	private ConnManager cmanager = new ConnManager() ;
	private ChatListener listener = ChatListener.NONE;
	private Mediator mediator ;
	private Snooper snooper ;
	
	private ChatEngine(Aradon aradon, EngineConfiguration econfig) {
		this.aradon = aradon ;
		this.econfig = econfig ;
		this.aradon.getServiceContext().putAttribute(ChatEngine.class.getCanonicalName(), this) ;
		this.mediator = Mediator.create(this) ;
		this.snooper = Snooper.NOTDEFINED ;
	}

	public final static ChatEngine create(String configPath) throws ConfigurationException, Exception {
		File file = new File(configPath);
		if (!file.exists()) throw new IllegalStateException("config file not found : " + configPath) ;
		
		ServerConfigParser sconfig = ServerConfigParser.parse(XMLConfig.create(file)) ;
		ChatEngine engine = sconfig.getTalkEngine() ;
		return engine ;
	}
	
	public final static ChatEngine createWithServerConfig() throws ConfigurationException, Exception{
		return create("./resource/config/server-config.xml") ;
	}
	
	public static ChatEngine createWithServerConfig(String engineId, int port) throws ConfigurationException, Exception {
		ChatEngine engine = createWithServerConfig() ;
		engine.getConfiguration().setId(engineId) ;
		engine.getConfiguration().setPort(port) ;
		return engine;
	}

	
	public final static ChatEngine create(Aradon aradon, EngineConfiguration econfig){
		return new ChatEngine(aradon, econfig) ;
	}
	
	public final static ChatEngine test(Aradon aradon){
		return new ChatEngine(aradon, new TestEngineConfig()) ;
	}
	
	public final static ChatEngine testWithEmptyAradon() throws Exception{
		Aradon aradon = new Aradon() ;
		aradon.init(XMLConfig.BLANK) ;
		return test(aradon) ;
	}
	
	
	public void onOpen(WebSocketConnection conn) {
		final UserConnector ucon = UserConnector.create(this, conn);
		
		cmanager.addConnection(ucon) ;
		
		eachPlugIn(new Closure<ChatPlugIn>() {
			public void execute(ChatPlugIn plugin) {
				plugin.connectorStarted(ucon);
			}
		});
		
		listener.processOpened(ChatEvent.create(ucon, this)) ;
	}

	public void onClose(WebSocketConnection conn) {
		final UserConnector ucon = UserConnector.create(this, conn);
		
		eachPlugIn(new Closure<ChatPlugIn>() {
			public void execute(ChatPlugIn plugin) {
				plugin.connectorStopped(ucon, CloseReason.CLIENT);
			}
		});
		listener.processClosed(ChatEvent.create(ucon, this)) ;
		cmanager.removeConnection(ucon, CloseReason.CLIENT) ;
	}
	
	public void onMessage(final WebSocketConnection conn, String msg)  {

		final IUserConnector ucon = find(conn) ;
		if (ucon == null) return ;

		final IMessagePacket packet = ChatMessage.create(msg);
		final PlugInResponse response = new PlugInResponse();

		for(ChatPlugIn plugin : plugins){
			plugin.processPacket(response, ucon, packet) ;
			if (response.isChainAborted()) break ;
		}
		
		listener.processPacket(ChatEvent.create(ucon, this), packet) ;
	}
	

	public void onMessage(WebSocketConnection conn, byte[] msg) {
		throw ChatRuntimeException.create("not yet supported");
	}
	
	public UserConnector[] getAllConnectors() {
		return cmanager.getAllConnectors();
	}


	private void eachPlugIn(Closure<ChatPlugIn> closure){
		CollectionUtil.each(plugins, closure);
	}
	
	public void eachConn(Closure<IUserConnector> clos){
		CollectionUtil.each(cmanager.values(), clos) ;
	}
	
	public void onPong(WebSocketConnection conn, String msg)  {
		; // not impl
	}

	public void onEvent(EventType event, WebServer wserver) {
		if (event == EventType.START) {
			aradon.start() ;
			eachPlugIn(new Closure<ChatPlugIn>() {
				public void execute(ChatPlugIn plugin) {
					plugin.engineStarted(aradon.getServiceContext());
				}
			});
		} else if (event == EventType.STOP) {
			eachPlugIn(new Closure<ChatPlugIn>() {
				public void execute(ChatPlugIn plugin) {
					plugin.engineStopped(aradon.getServiceContext());
				}
			});
			aradon.stop() ;
		}
	}

	public List<ChatPlugIn> getPlugIns() {
		return plugins;
	}

	public void addPlugIn(ChatPlugIn plugin) {
		plugins.add(plugin);
		plugin.setEngine(this) ;
	}

	public void removePlugIn(ChatPlugIn plugin) {
		plugins.remove(plugin);
		plugin.setEngine(null) ;
	}

	public void clear() {
		plugins.clear();
	}

	public Aradon getAradon() {
		return aradon;
	}
	
	private IUserConnector find(WebSocketConnection conn) {
		return cmanager.findByUserId(conn.getString(ChatConstants.VAR_USERID)) ;
		
	}

	public IUserConnector findByUserId(String userId){
		return cmanager.findByUserId(userId) ;
	}


	public <T> T getAttributeObject(String canonicalName, Class<T> clz) {
		return aradon.getServiceContext().getAttributeObject(canonicalName, clz) ;
	}
	
	public void putAttribute(String key, Object value) {
		aradon.getServiceContext().putAttribute(key, value) ;
	}

	public String getId() {
		return econfig.getId();
	}

	public void setListener(ChatListener listener) {
		if (listener == null) return ;
		this.listener = listener ; 
	}

	public EngineConfiguration getConfiguration() {
		return econfig;
	}

	
	private static class ConnManager{
		private Map<String, IUserConnector> conns = MapUtil.newSyncMap() ;
		
		public void addConnection(UserConnector conn){
			// @TODO if exist ?
			IUserConnector existConn = conns.get(conn.getUserId()) ;
			if (existConn != null) existConn.stopConnector(CloseReason.DOPPELGANGER) ;
			
			conns.put(conn.getUserId(), conn) ;
		}
		
		public Collection<IUserConnector> values() {
			return new ArrayList<IUserConnector>(conns.values());
		}

		public UserConnector[] getAllConnectors() {
			return conns.values().toArray(new UserConnector[0]) ;
		}

		public void removeConnection(IUserConnector conn, CloseReason reason){
			conns.remove(conn.getUserId()) ;
			conn.close() ;
		}
		
		public IUserConnector findByUserId(String userId){
			return conns.get(userId) ;
		}
	}

	public void send(IUserConnector sender, String targetUserId, NormalMessagePacket newMsg) {
		Target.create(this, sender, targetUserId).send(newMsg) ;
	}

	public ChatListener getListener() {
		return this.listener;
	}

	public void set(ChatServer toonServer, WebServer server) {
		AradonHandler ahandler = AradonHandler.create(aradon);
		ahandler.ignoreEvent(true) ;
		
		OutLogSink logsink = new OutLogSink(getConfiguration(), ChatConstants.VAR_USERID);
		this.snooper = logsink ;
		
		server.add(new LoggingHandler(logsink)) ;
		server.add("/toon/.*/.*", new HttpToWebSocketHandler(this)) ;
		server.add("/aradon/.*", ahandler) ;
		server.add("/plugin.*", ahandler) ;
		server.add("/session/.*", ahandler) ;
		server.add("/toonweb/.*", new StaticFileHandler("./resource/")) ;
		server.add("/event/.*", logsink) ;
		
		
		aradon.getServiceContext().putAttribute(ChatServer.class.getCanonicalName(), toonServer) ; // for stop
	}
	
	
	public Mediator getMediator(){
		return mediator ;
	}

	public void shutdown() {
		eachConn(new Closure<IUserConnector>() {

			public void execute(IUserConnector obj) {
				obj.stopConnector(CloseReason.SHUTDOWN) ;
			}
		}) ;
	}

	public Tracer getTracer(String userId) {
		return snooper.getTracer(userId) ;
	}

	
	public Snooper getSnooper(){
		return snooper ;
	}

}


abstract class Target {

	public static Target create(ChatEngine engine, IUserConnector sender, String targetId) {
		IUserConnector target = engine.findByUserId(targetId) ;
		if (target == null){
			return new NotConnectedUser(engine, sender.getUserBean(), targetId) ;
		} else {
			return new ConnectedUser(target) ;
		}
	}

	public abstract Target send(IMessagePacket msg) ;

}


class ConnectedUser extends Target{

	private IUserConnector targetConn ;
	ConnectedUser(IUserConnector targetConn) {
		this.targetConn = targetConn ;
	}
	
	public Target send(IMessagePacket msg) {
		targetConn.send(msg) ;
		return this ;
	}

}

class NotConnectedUser extends Target {

	private ChatEngine engine ;
	private UserBean sender ;
	private String targetId ;
	NotConnectedUser(ChatEngine engine, UserBean sender, String targetId) {
		this.engine = engine ;
		this.sender = sender ;
		this.targetId = targetId ;
	}
	
	@Override
	public Target send(IMessagePacket msg) {
		engine.getMediator().saveAt(sender, targetId, msg) ;
		engine.getSnooper().getTracer(targetId).saveAtMediator(msg) ;
		return this;
	}
	
}


class DefaultEngineConfig implements EngineConfiguration {

	private XMLConfig config ;
	private int port ;
	private String id ;
	
	DefaultEngineConfig(XMLConfig config) {
		this.config = config ;
		this.port = config.getInt("[@port]", 8080);
		this.id = config.getString("[@id]", "toon") ;
	}

	public String getURIPath() {
		return config.getString("[@uripath]", "");
	}
	
	public int getMaxFramesize() {
		return config.getInt("[@max-frame-size]", 16384);
	}

	public int getPort() {
		return port;
	}
	
	public void setId(String id){
		this.id = id ;
	}
	
	public void setPort(int port){
		this.port = port ;
	}

	public int getTimeout() {
		return config.getInt("[@timeout]", 5000);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return config.getString("[@name]", "Toon");
	}
}


class TestEngineConfig implements EngineConfiguration {

	private int port = 9000;
	public final static String TEST_ID = "mercury";
	private String id = "mercury";

	public String getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return 5000;
	}

	public String getURIPath() {
		return "/{$serviceName}/{$userId}/{$authId}";
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setId(String id) {
		this.id = id;
	}
}

