package net.ion.chat.handler;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;

public interface Tracer {

	public static final Tracer NONE = new Tracer(){
		public List<Map<String, ? extends Object>> trail(PageBean page) {
			return ListUtil.EMPTY;
		}

		public void saveAtMediator(IMessagePacket msg) {

		}

		public Representation toRepresentation(PageBean page) {
			return new StringRepresentation("not supported");
		}
	} ;
	
	public List<Map<String, ? extends Object>> trail(PageBean page) ;

	public void saveAtMediator(IMessagePacket msg);

	public Representation toRepresentation(PageBean page);


}
