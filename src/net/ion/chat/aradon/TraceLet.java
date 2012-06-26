package net.ion.chat.aradon;

import java.util.List;
import java.util.Map;

import net.ion.chat.handler.ChatEngine;
import net.ion.chat.handler.Tracer;
import net.ion.radon.core.PageBean;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class TraceLet extends AbstractServerResource{

	@Get
	public Representation viewTrace() {
		
		String userId = getInnerRequest().getAttribute("userId") ;
		PageBean page = getInnerRequest().getAradonPage() ;
		
		ChatEngine engine = getContext().getAttributeObject(ChatEngine.class.getCanonicalName(), ChatEngine.class) ;
		Tracer tracer =  engine.getTracer(userId) ;
		
		
		return tracer.toRepresentation(page) ;
	}
	
}
