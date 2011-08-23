package net.ion.websocket.aradon.front;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.aradon.websocket.ClientWrapper;
import net.ion.aradon.websocket.IClientWrapper;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.websocket.client.SyncMockClient;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class ServersLet extends AbstractServerResource{
	
	@Get
	public Representation listServer() throws Exception {
		
		ClientWrapper cw = getContext().getAttributeObject(IClientWrapper.class.getCanonicalName(), ClientWrapper.class) ;
		
		List<Map<String, ?>> data = ListUtil.newList();
		for (Entry<URI, SyncMockClient> entry : cw.getServerList().entrySet()) {
			Map<String, Object> sinfo = MapUtil.newMap() ;
			sinfo.put("id", entry.getValue().getUserName()) ;
			sinfo.put("uri", entry.getKey().toString()) ;
			sinfo.put("status", entry.getValue().getStatus()) ;
			sinfo.put("last", entry.getValue().getLastPacket()) ;
			
			data.add(sinfo) ;
		}
		
		return toRepresentation(data) ;
	}
}
