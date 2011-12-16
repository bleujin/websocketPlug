//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket NIO Engine, DataFuture
//	Copyright (c) 2011 Innotrade GmbH, jWebSocket.org, Author: Jan Gnezda
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
package net.ion.websocket.server.engine.tcp.nio;

import org.apache.log4j.Logger;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.async.IOFuture;
import net.ion.websocket.common.async.IOFutureListener;
import net.ion.websocket.common.logging.Logging;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataFuture implements IOFuture {

	private static Logger logger = Logging.getLogger(DataFuture.class);
	private List<IOFutureListener> listeners;
	private boolean done;
	private boolean success;
	private Throwable cause;
	private WebSocketConnector connector;
	private ByteBuffer data;

	public DataFuture(WebSocketConnector connector, ByteBuffer data) {
		this.connector = connector;
		this.data = data;
		listeners = new ArrayList<IOFutureListener>();
	}

	@Override
	public WebSocketConnector getConnector() {
		return connector;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public boolean isCancelled() {
		return false;  // not implemented
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}

	@Override
	public boolean cancel() {
		return false;  // not implemented
	}

	@Override
	public boolean setSuccess() {
		success = true;
		done = true;
		notifyListeners();
		return success;
	}

	@Override
	public boolean setFailure(Throwable cause) {
		if (!success && !done) {
			this.cause = cause;
			success = false;
			done = true;
			notifyListeners();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean setProgress(long amount, long current, long total) {
		return false;  // not implemented
	}

	@Override
	public void addListener(IOFutureListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(IOFutureListener listener) {
		listeners.remove(listener);
	}

	public ByteBuffer getData() {
		return data;
	}

	private void notifyListeners() {
		try {
			for (IOFutureListener listener : listeners) {
				listener.operationComplete(this);
			}
		} catch (Exception e) {
			logger.info("Exception while notifying IOFuture listener", e);
		}
	}
}
