package net.ion.nchat.context;

import java.io.File;
import java.io.IOException;

import net.ion.craken.Craken;
import net.ion.nchat.util.FileFinder;
import net.ion.radon.core.IService;
import net.ion.radon.core.context.OnOrderEventObject;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;

public class ChatCraken implements OnOrderEventObject {

	private final String clusterName;
	private final String configPath;
	private Craken craken;

	public ChatCraken(String clusterName, String configPath) {
		this.clusterName = clusterName;
		this.configPath = configPath;
	}

	public final static ChatCraken test() {
		return new ChatCraken("my-cluster", "resource/config/jgroups-udp.xml");
	}

	@Override
	public void onEvent(AradonEvent ae, IService service) {
		if (ae == AradonEvent.START) {
			try {
				this.craken = Craken.create();
				FileFinder ff = service.getServiceContext().getAttributeObject(FileFinder.class.getCanonicalName(), FileFinder.class) ;
				File configFile = ff.findFile(service, configPath);
				
				craken.globalConfig().transport().clusterName(clusterName).addProperty("configurationFile", configFile.getCanonicalPath());
				craken.preDefineConfig(SerializedChatMessage.class, new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_ASYNC).clustering().eviction().maxEntries(10000).strategy(EvictionStrategy.LIRS).build());
				craken.start();
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		} else if (ae == AradonEvent.STOP) {
		}
	}

	public Craken getCraken() {
		if (craken == null)
			throw new IllegalStateException("not initialized craken");
		return craken;
	}

	@Override
	public int order() {
		return 0; // first
	}
}
