// ---------------------------------------------------------------------------
// jWebSocket - Copyright (c) 2010 jwebsocket.org
// ---------------------------------------------------------------------------
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by the
// Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
// more details.
// You should have received a copy of the GNU Lesser General Public License along
// with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
// ---------------------------------------------------------------------------
package net.ion.websocket.server.engine.netty.connector;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import net.ion.websocket.common.async.IOFutureListener;

/**
 * Netty channel future listener implementation to support NIO listener implementation
 * 
 * @author puran
 * @version $Id: NIOInternalFutureListener.java,v 1.1 2011/12/15 06:30:24 bleujin Exp $
 */
public class NIOInternalFutureListener implements ChannelFutureListener {

	private IOFutureListener listener = null;
	private NIOFuture source = null;

	public NIOInternalFutureListener(NIOFuture theSource, IOFutureListener theListener) {
		this.listener = theListener;
		this.source = theSource;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		listener.operationComplete(source);
	}
}
