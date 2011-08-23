//	---------------------------------------------------------------------------
//	jWebSocket - BaseFilterChain Implementation
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

import java.util.List;

import javolution.util.FastList;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketFilter;
import net.ion.websocket.common.api.WebSocketFilterChain;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.kit.FilterResponse;
import net.ion.websocket.common.logging.Logging;

import org.apache.log4j.Logger;

/**
 * 
 * @author aschulze
 */
public class BaseFilterChain implements WebSocketFilterChain {

	private static Logger log = Logging.getLogger(BaseFilterChain.class);
	private List<WebSocketFilter> filters = new FastList<WebSocketFilter>();
	private WebSocketServer server = null;

	/**
	 * 
	 * @param server
	 */
	public BaseFilterChain(WebSocketServer server) {
		this.server = server;
	}

	/**
	 * @return the server
	 */
	public WebSocketServer getServer() {
		return server;
	}

	public void addFilter(WebSocketFilter filter) {
		filters.add(filter);
		filter.setFilterChain(this);
	}

	public void removeFilter(WebSocketFilter filter) {
		filters.remove(filter);
		filter.setFilterChain(null);
	}

	/**
	 * 
	 * @return
	 */
	public List<WebSocketFilter> getFilters() {
		return filters;
	}

	public FilterResponse processPacketIn(WebSocketConnector connector) {
		FilterResponse response = new FilterResponse();
		for (WebSocketFilter filter : filters) {
			filter.processPacketIn(response, connector);
			if (response.isRejected()) {
				break;
			}
		}
		return response;
	}

	public FilterResponse processPacketOut(WebSocketConnector source) {
		FilterResponse response = new FilterResponse();
		for (WebSocketFilter filter : filters) {
			filter.processPacketOut(response, source);
			if (response.isRejected()) {
				break;
			}
		}
		return response;
	}

	public void clear() {
		filters.clear();
	}
}
