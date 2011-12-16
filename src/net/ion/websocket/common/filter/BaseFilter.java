//	---------------------------------------------------------------------------
//	jWebSocket - BaseFilter Implementation
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
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
package net.ion.websocket.common.filter;

import net.ion.websocket.common.api.WebSocketPacket;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketFilter;
import net.ion.websocket.common.api.WebSocketFilterChain;
import net.ion.websocket.common.config.FilterConfiguration;
import net.ion.websocket.common.kit.FilterResponse;

/**
 * 
 * @author aschulze
 */
public class BaseFilter implements WebSocketFilter {


	private WebSocketFilterChain filterChain = null;
	private FilterConfiguration config = null;

	public BaseFilter() {
		this(FilterConfiguration.BLANK) ;
	}

	public BaseFilter(FilterConfiguration config) {
		this.config = (config == null) ? FilterConfiguration.BLANK : config;
	}

	@Override
	public String toString() {
		return config.getId();
	}

	@Override
	public void processPacketIn(FilterResponse response, WebSocketConnector connector, WebSocketPacket packet) {
	}

	@Override
	public void processPacketOut(FilterResponse response, WebSocketConnector source, WebSocketConnector target, WebSocketPacket packet) {
	}

	/**
	 * 
	 * @param filterChain
	 */
	@Override
	public void setFilterChain(WebSocketFilterChain filterChain) {
		filterChain = filterChain;
	}

	/**
	 * @return the filterChain
	 */
	@Override
	public WebSocketFilterChain getFilterChain() {
		return filterChain;
	}

	/**
	 * @return the id of the filter
	 */
	@Override
	public String getId() {
		return config.getNamespace();
	}

	/**
	 * @return the name space of the filter
	 */
	@Override
	public String getNS() {
		return config.getId();
	}
}
