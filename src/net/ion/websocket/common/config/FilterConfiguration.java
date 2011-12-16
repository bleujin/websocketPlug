//  ---------------------------------------------------------------------------
//  jWebSocket - Copyright (c) 2010 jwebsocket.org
//  ---------------------------------------------------------------------------
//  This program is free software; you can redistribute it and/or modify it
//  under the terms of the GNU Lesser General Public License as published by the
//  Free Software Foundation; either version 3 of the License, or (at your
//  option) any later version.
//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  You should have received a copy of the GNU Lesser General Public License along
//  with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//  ---------------------------------------------------------------------------
package net.ion.websocket.common.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ion.websocket.common.api.Configuration;

/**
 * Base interface for filter configuration
 * 
 * @author puran
 * @version $Id: FilterConfiguration.java,v 1.1 2011/12/15 06:30:22 bleujin Exp $
 */
public interface FilterConfiguration extends Configuration {
	
	public final static FilterConfiguration BLANK = new FilterConfiguration() {
		
		@Override
		public String getName() {
			return null;
		}
		
		@Override
		public String getId() {
			return null;
		}
		
		@Override
		public Map<String, String> getSettings() {
			return Collections.EMPTY_MAP;
		}
		
		@Override
		public String getPackageName() {
			return null;
		}
		
		@Override
		public String getNamespace() {
			return null;
		}
		
		@Override
		public String getJar() {
			return null;
		}
	};
	
	String getJar();

	String getPackageName();

	String getNamespace();

	Map<String, String> getSettings();
}
