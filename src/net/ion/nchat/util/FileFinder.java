package net.ion.nchat.util;

import java.io.File;
import java.io.IOException;

import net.ion.radon.core.IService;
import net.ion.radon.core.config.PlugInsConfiguration;
import net.ion.radon.core.context.OnOrderEventObject;

public class FileFinder implements OnOrderEventObject {

	private String pluginId;

	public FileFinder(String pluginId) {
		this.pluginId = pluginId;
	}

	public File findFile(IService service, String relativePath) {
		try {
			PlugInsConfiguration pluginConfig = service.getAradon().getGlobalConfig().plugin();
			File foundFile = null ;
			try {
				foundFile = pluginConfig.findPlugInFile(pluginId, "/" + relativePath);
			} catch(IllegalArgumentException notfound){
				foundFile = new File(relativePath);
			}
			if (!foundFile.exists())
				throw new IllegalStateException(foundFile.getCanonicalPath() + " not exists");
			return foundFile;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public int order() {
		return -1;
	}

	@Override
	public void onEvent(AradonEvent event, IService service) {
		// TODO Auto-generated method stub

	}
}
