//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package net.ion.websocket.common.config.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import javolution.util.FastList;
import net.ion.websocket.common.config.Config;
import net.ion.websocket.common.config.ConfigHandler;

/**
 * Handler class to read roles
 * 
 * @author puran
 * @version $Id: RoleConfigHandler.java,v 1.2 2011/07/23 04:35:54 bleujin Exp $
 * 
 */
public class RoleConfigHandler implements ConfigHandler {

	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String ELEMENT_RIGHT = "right";
	private static final String ELEMENT_RIGHTS = "rights";
	private static final String ELEMENT_ROLE = "role";

	/**
	 * {@inheritDoc}
	 */
	public Config processConfig(XMLStreamReader streamReader) throws XMLStreamException {
		String id = "", description = "";
		List<String> rights = new FastList<String>();
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ID)) {
					streamReader.next();
					id = streamReader.getText();
				} else if (elementName.equals(DESCRIPTION)) {
					streamReader.next();
					description = streamReader.getText();
				} else if (elementName.equals(ELEMENT_RIGHTS)) {
					streamReader.next();
					rights = getRights(streamReader);
				} else {
					// ignore
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_ROLE)) {
					break;
				}
			}
		}
		return new RoleConfig(id, description, rights);
	}

	/**
	 * private method that reads the list of rights from the role configuration
	 * 
	 * @param streamReader
	 *            the stream reader object
	 * @return the list of right ids
	 * @throws XMLStreamException
	 *             if exception while reading
	 */
	private List<String> getRights(XMLStreamReader streamReader) throws XMLStreamException {
		List<String> rights = new FastList<String>();
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_RIGHT)) {
					streamReader.next();
					String right = streamReader.getText();
					rights.add(right);
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_RIGHTS)) {
					break;
				}
			}
		}
		return rights;
	}

}
