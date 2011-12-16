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
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketFilter;
import net.ion.websocket.common.api.WebSocketFilterChain;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.kit.FilterResponse;
import net.ion.websocket.common.logging.Logging;

import org.apache.log4j.Logger;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.filter.BaseFilterChain;

/**
 * 
 * @author aschulze
 */
public class BaseFilterChain implements WebSocketFilterChain {

	private static Logger logger = Logging.getLogger(BaseFilterChain.class);
	private Map<String, WebSocketFilter> filterMap = new FastMap<String, WebSocketFilter>();
	private WebSocketServer server = null;

	/**
	 *
	 * @param server
	 */
	public BaseFilterChain(WebSocketServer server) {
		server = server;
	}

	/**
	 * @return the server
	 */
	public WebSocketServer getServer() {
		return server;
	}

	@Override
	public void addFilter(WebSocketFilter filter) {
		filterMap.put(filter.getId(), filter);
		filter.setFilterChain(this);
	}

	@Override
	public void removeFilter(WebSocketFilter filter) {
		filterMap.remove(filter.getId());
		filter.setFilterChain(null);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public List<WebSocketFilter> getFilters() {
		return new FastList<WebSocketFilter>(filterMap.values());
	}

	@Override
	public WebSocketFilter getFilterById(String aId) {
		return filterMap.get(aId);
	}

	@Override
	public FilterResponse processPacketIn(WebSocketConnector connector, WebSocketPacket packet) {
		FilterResponse lResponse = new FilterResponse();
		for (WebSocketFilter lFilter : filterMap.values()) {
			lFilter.processPacketIn(lResponse, connector, packet);
			if (lResponse.isRejected()) {
				break;
			}
		}
		return lResponse;
	}

	@Override
	public FilterResponse processPacketOut(WebSocketConnector source, WebSocketConnector target, WebSocketPacket packet) {
		FilterResponse response = new FilterResponse();
		for (WebSocketFilter filter : filterMap.values()) {
			filter.processPacketOut(response, source, target, packet);
			if (response.isRejected()) {
				break;
			}
		}
		return response;
	}
}
