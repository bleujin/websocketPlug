package net.ion.nchat.context;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;

import net.ion.craken.Craken;
import net.ion.radon.core.IService;
import net.ion.radon.core.context.OnOrderEventObject;
import net.ion.radon.core.context.OnEventObject.AradonEvent;

public class ChatCraken implements OnOrderEventObject{

	private Craken craken ;
	public ChatCraken(String clusterName, String configPath) {
		this.craken = Craken.create() ;
		craken.globalConfig().transport().clusterName(clusterName).addProperty("configurationFile", configPath) ;
	}
	
	public final static ChatCraken test(){
		return new ChatCraken("my-cluster", "resource/config/jgroups-udp.xml") ;
	}

	@Override
	public void onEvent(AradonEvent ae, IService service) {
		if (ae == AradonEvent.START){
			craken.preDefineConfig(SerializedChatMessage.class, new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_ASYNC).clustering().eviction().maxEntries(10000).strategy(EvictionStrategy.LIRS).build()) ;
			craken.start() ;
			
		} else if (ae == AradonEvent.STOP){
			craken.stop() ;
		}
	}

	public Craken getCraken() {
		return craken ;
	}

	@Override
	public int order() {
		return 0; // first
	}
}
