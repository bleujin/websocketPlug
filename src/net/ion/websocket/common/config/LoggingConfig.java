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
package net.ion.websocket.common.config;

import net.ion.websocket.common.kit.WebSocketRuntimeException;


/**
 * Configuration for logging
 * User: puran
 *
 * @version $Id: LoggingConfig.java,v 1.2 2011/07/23 04:35:53 bleujin Exp $
 */
public class LoggingConfig implements Config {

	private final String appender;
	private final String pattern;
	private final String level;
	private final String filename;
	private final Integer bufferSize;

	/**
	 * Costrutor
	 *
	 * @param appender the logging appender
	 * @param pattern  logging pattern
	 * @param level    the level of logging
	 * @param filename the log file name
	 */
	public LoggingConfig(String appender, String pattern, String level,
			String filename, Integer aBufferSize) {
		this.appender = appender;
		this.pattern = pattern;
		this.level = level;
		this.filename = filename;
		this.bufferSize = aBufferSize;
	}

	public String getAppender() {
		return appender;
	}

	public String getPattern() {
		return pattern;
	}

	public String getLevel() {
		return level;
	}

	public String getFilename() {
		return filename;
	}

	public Integer getBufferSize() {
		return bufferSize;
	}

	/**
	 * {@inheritDoc}
	 */
	public void validate() {
		if ((appender != null && appender.length() > 0)
				&& (pattern != null && pattern.length() > 0)
				&& (level != null && level.length() > 0)
				&& (filename != null && filename.length() > 0)
				&& (bufferSize != null && bufferSize >= 0)) {
			return;
		}
		throw new WebSocketRuntimeException(
				"Missing one of the logging configuration directives, "
				+ "please check your configuration file");
	}
}
