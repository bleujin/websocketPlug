package net.ion.nchat.aradon;

import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nchat.async.SampleChatHandler;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.core.let.DefaultLet;
import net.ion.radon.core.let.WSPathService;

import org.restlet.representation.Representation;

public class SampleMonitorLet extends DefaultLet {

	@Override
	protected Representation myGet() throws Exception {
		
		WSPathService wspath =  getSectionService().getAradon().getChildService("async").wspath("chat") ;
		SampleChatHandler handler = (SampleChatHandler)wspath.websocketResource();
		
		List<WebSocketConnection> conns = handler.getAllConnectors();
		
		List<Map<String, ?>> data = ListUtil.newList() ;
		for (WebSocketConnection conn : conns) {
			Map<String, Object> cinfo = MapUtil.newMap() ;
			cinfo.put(WSPathService.VAR_SESSIONID, conn.data(WSPathService.VAR_SESSIONID)) ;
			cinfo.put("datas", conn.httpRequest().data() ) ;
			cinfo.put("remoteAddress", conn.httpRequest().remoteAddress()) ;
			cinfo.put("timestamp", conn.httpRequest().timestamp()) ;
			cinfo.put("headers", conn.httpRequest().allHeaders()) ;
			cinfo.put("cookies", conn.httpRequest().cookies()) ;
			data.add(cinfo) ;
		}
		
		return toRepresentation(data); 
	}

}
