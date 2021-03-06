package net.ion.nchat.aradon;

import java.io.File;
import java.io.IOException;

import net.ion.nchat.util.FileFinder;
import net.ion.radon.core.let.AbstractServerResource;

import org.apache.commons.io.FileUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class IndexHtmlLet extends AbstractServerResource{

	
	@Get
	public Representation readHTMLFile() throws IOException{

		String filePath = getContext().getAttributeObject(IndexHtmlLet.class.getCanonicalName(), "resource/toonweb/embed-index.html", String.class) ;
		FileFinder ff = getContext().getAttributeObject(FileFinder.class.getCanonicalName(), FileFinder.class) ;
		
		File file = ff.findFile(getMySectionService(), filePath);
		String result = "not found file. confirm context attribute[" + IndexHtmlLet.class.getCanonicalName() + "]" ;
		if (file.exists()){
			result = FileUtils.readFileToString(file, "UTF-8") ;
		}
		
		StringRepresentation rep = new StringRepresentation(result) ;
		rep.setMediaType(MediaType.TEXT_HTML) ;
		
		return rep ;
	}
	
	@Post
	public Representation readPost() throws IOException{
		return readHTMLFile() ;
	}
}
