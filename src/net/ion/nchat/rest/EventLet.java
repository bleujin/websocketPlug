package net.ion.nchat.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import net.ion.framework.util.IOUtil;
import net.ion.nchat.util.FileFinder;
import net.ion.radon.core.let.AbstractServerResource;

import org.antlr.stringtemplate.StringTemplate;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class EventLet extends AbstractServerResource{
	
	@Get
	public Representation viewPage() throws FileNotFoundException, IOException, URISyntaxException {
		
		String topicId = getInnerRequest().getAttribute("topicId") ;
		
		String tplFileName = getContext().getAttributeObject(EventLet.class.getCanonicalName(), "./resource/toonweb/event.tpl", String.class) ;
		FileFinder ff = getContext().getAttributeObject(FileFinder.class.getCanonicalName(), FileFinder.class) ;
		
		File tplFile = ff.findFile(getMySectionService(), tplFileName);
		if (! tplFile.exists()) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "not found template file : " + tplFile.getCanonicalPath()) ;
		
		StringTemplate st = new StringTemplate(IOUtil.toString(new FileInputStream(tplFile))) ;
		st.setAttribute("topicId", topicId) ;
		
		return new StringRepresentation(st.toString(), MediaType.TEXT_HTML, Language.ALL) ;
	}
}
