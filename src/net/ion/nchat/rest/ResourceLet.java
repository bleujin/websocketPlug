package net.ion.nchat.rest;

import java.io.File;

import net.ion.framework.util.PathMaker;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class ResourceLet extends AbstractServerResource {

	@Get
	public Representation viewImage() {
		String remainPath = getInnerRequest().getRemainPath() ;
		// File file = getAradon().getGlobalConfig().plugin().findPlugInFile("net.bleujin.sample.chat", "/resource/toonweb/" + remainPath);
		File file = new File(PathMaker.getFilePath(getContext().getAttributeObject("base.dir", "./resource/toonweb/", String.class), remainPath)) ;
		
		if (! file.exists()) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getRequest().getResourceRef().getPath()) ; 

		MediaType mtype = getMetadataService().getMediaType(StringUtil.substringAfterLast(file.getName(), ".")) ;
		if (mtype == null) mtype = MediaType.ALL ; 
		
		final FileRepresentation result = new FileRepresentation(file, mtype);
		return result ;
	}
}
