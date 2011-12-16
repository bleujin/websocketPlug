//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket Exception
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
package net.ion.websocket.common.kit;

/**
 * Exception class to represent JWebSocketServer related exception
 * @author Puran Singh
 * @version $Id: WebSocketRuntimeException.java,v 1.2 2011/12/15 06:30:18 bleujin Exp $
 *
 */
public class WebSocketRuntimeException extends RuntimeException {

	/**
	 * creates the exception with given message
	 * @param error the error messae
	 */
	public WebSocketRuntimeException(String error) {
		super(error);
	}
	
	/**
	 * creates the exception with given message
	 * @param error the error messae
	 * @param throwable the cause 
	 */
	public WebSocketRuntimeException(String error, Throwable throwable) {
		super(error, throwable);
	}

	private static final long serialVersionUID = 1L;
	
}
