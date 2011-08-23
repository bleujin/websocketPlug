//---------------------------------------------------------------------------
//jWebSocket - jWebSocket Sample Plug-In
//Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
//---------------------------------------------------------------------------
//This program is free software; you can redistribute it and/or modify it
//under the terms of the GNU Lesser General Public License as published by the
//Free Software Foundation; either version 3 of the License, or (at your
//option) any later version.
//This program is distributed in the hope that it will be useful, but WITHOUT
//ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//more details.
//You should have received a copy of the GNU Lesser General Public License along
//with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//---------------------------------------------------------------------------

package net.ion.websocket.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javolution.util.FastMap;
import net.ion.websocket.common.api.EngineConfiguration;
import net.ion.websocket.common.api.ServerConfiguration;
import net.ion.websocket.common.api.WebSocketFilter;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.api.WebSocketServer;

/**
 * Example of custom JWebSocket intializer to initalize and register the custom
 * plugins, servers and filters to jWebSocket infrastructure. This is for development mode
 * so that developers can debug their plugins, filters etc.. at compile time.
 * @author puran
 * @version $Id: CustomInitializer.java,v 1.1 2011/07/23 04:35:53 bleujin Exp $
 * 
 */
public class CustomInitializer extends AbstractJWebSocketInitializer {

    @Override
    public FastMap<String, List<WebSocketPlugIn>> initializeCustomPlugins() {
        FastMap<String, List<WebSocketPlugIn>> pluginMap = new FastMap<String, List<WebSocketPlugIn>>();
        List<WebSocketPlugIn> plugins = new ArrayList<WebSocketPlugIn>();
        plugins.add(new SamplePlugIn());
        pluginMap.put("ts0", plugins);
        return pluginMap;
    }

    @Override
    public List<WebSocketServer> initializeCustomServers() {
        return Collections.emptyList();
    }

    @Override
    public EngineConfiguration getEngineConfiguration() {
        return null;
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return null;
    }

    @Override
    public FastMap<String, List<WebSocketFilter>> initializeCustomFilters() {
        return null;
    }

}
