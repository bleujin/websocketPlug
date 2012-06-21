package net.ion.chat.handler;

import java.util.List;
import java.util.Map;

import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;

public interface Tracer {

	public static final Tracer NONE = new Tracer(){
		public List<Map<String, ? extends Object>> trail(PageBean page) {
			return ListUtil.EMPTY;
		}

		public void saveAtMediator(IMessagePacket msg) {
			// TODO Auto-generated method stub
			
		}
	} ;
	
	public List<Map<String, ? extends Object>> trail(PageBean page) ;

	public void saveAtMediator(IMessagePacket msg);


}
