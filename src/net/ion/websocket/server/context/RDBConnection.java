package net.ion.websocket.server.context;

import java.util.List;

import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.servant.ChannelServant;
import net.ion.framework.db.servant.IExtraServant;
import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.config.XMLConfig;

import org.apache.commons.configuration.ConfigurationException;

public class RDBConnection {

	public static IDBController createDC(final XMLConfig xmlConfig) throws InstanceCreationException, ConfigurationException {
		
		XMLConfig dbConfig = xmlConfig.firstChild("database-controller") ;
		
		String name = dbConfig.getString("controller-name") ;
		DBManager dbm = (DBManager) ConfigCreator.createConfiguredInstance(dbConfig.firstChild("database-manager.configured-object")) ;
		
		List<XMLConfig> cconfig = dbConfig.children("extra-servant.configured-object");
		
		ChannelServant cservant = new ChannelServant() ;
		for (XMLConfig sconfig : cconfig) {
			IExtraServant nextServant = (IExtraServant) ConfigCreator.createConfiguredInstance(sconfig);
			cservant.add(nextServant) ;
		}
		return new DBController(name, dbm, cservant) ;
	}
}
