package net.ion.chat.handler;

import java.util.List;

import net.ion.chat.api.ChatListener;
import net.ion.chat.config.EngineConfiguration;
import net.ion.chat.config.ListenerInfo;
import net.ion.chat.config.PlugInInfo;
import net.ion.chat.impl.listener.ChannelListener;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.XMLConfig;

import org.apache.commons.configuration.ConfigurationException;

public class ServerConfigParser {

	private XMLConfig root;
	private Aradon aradon = new Aradon();
	private ChatEngine engine;

	private EngineConfiguration econfig;

	private ServerConfigParser(XMLConfig root, String baseDir) {
		this.root = root;
	}

	public final static ServerConfigParser parse(XMLConfig root) throws Exception {
		return parse(root, "./");
	}

	public final static ServerConfigParser parse(XMLConfig root, String baseDir) throws Exception {
		ServerConfigParser result = new ServerConfigParser(root, baseDir);
		result.init();
		return result;
	}

	private void init() throws Exception {
		aradon.init(root);

		List<PlugInInfo> pluginInfos = makePlugInInfos(root.firstChild("plugins"));
		List<ListenerInfo> listenerInfos = makeListenerInfos(root.firstChild("listeners"));
		econfig = new DefaultEngineConfig(root.firstChild("engine"));

		Debug.line("PlugIn Load");
		this.engine = ChatEngine.create(aradon, econfig);
		for (PlugInInfo pinfo : pluginInfos) {
			engine.addPlugIn(pinfo.getPlugIn());
			Debug.info(pinfo.getName(), pinfo.getPlugIn().getClass(), pinfo.getDescription());
		}

		Debug.line("Listener Load");
		ChatListener[] listeners = new ChatListener[listenerInfos.size()];
		int i = 0;
		for (ListenerInfo iinfo : listenerInfos) {
			listeners[i++] = iinfo.getListener();
			Debug.info(iinfo.getName(), iinfo.getListener().getClass(), iinfo.getDescription());
		}
		engine.setListener(new ChannelListener(2000, listeners));
	}

	private List<ListenerInfo> makeListenerInfos(XMLConfig listeners) throws ConfigurationException, InstanceCreationException {
		List<XMLConfig> pluginConfig = listeners.children("listener");

		List<ListenerInfo> result = ListUtil.newList();
		for (XMLConfig config : pluginConfig) {
			result.add(ListenerInfo.create(config));
		}

		return result;
	}

	private List<PlugInInfo> makePlugInInfos(XMLConfig plugins) throws ConfigurationException, InstanceCreationException {
		List<XMLConfig> pluginConfig = plugins.children("plugin");

		List<PlugInInfo> result = ListUtil.newList();
		for (XMLConfig config : pluginConfig) {
			result.add(PlugInInfo.create(config));
		}

		return result;
	}

	public Aradon getAradon() {
		return this.aradon;
	}

	public ChatEngine getTalkEngine() {
		return this.engine;
	}

	public EngineConfiguration getEngineConfiguration() {
		return econfig;
	}
}
