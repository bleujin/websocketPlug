//	---------------------------------------------------------------------------
//	jWebSocket - Shared Logging Support
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
package net.ion.websocket.common.logging;

import java.io.IOException;

import net.ion.websocket.common.config.LoggingConfig;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * Provides the common used jWebSocket logging support based on Apache's log4j.
 * 
 * @author aschulze
 */
public class Logging {

	private static PatternLayout LAYOUT = null;
	private static Appender APPENDER = null;
	private static Level LOG_LEVEL = Level.DEBUG;
	private static String[] SEARCH_PATH = null;
	/**
	 * Log output is send to the console (stdout).
	 */
	public final static int CONSOLE = 0;
	/**
	 * Log output is send to a rolling file.
	 */
	public final static int ROLLING_FILE = 1;
	/**
	 * Log output is send to a single file.
	 */
	public final static int SINGLE_FILE = 2;
	/**
	 * Name of jWebSocket log file.
	 */
	private static String filename = "jWebSocket.log";
	/**
	 * Pattern for jWebSocket log file.
	 */
	private static String pattern = "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p - %C{1}: %m%n";
	/**
	 * Buffersize if write cache for logs is activated (recommended) Buffersize = 0 means no write cache.
	 */
	private static int BUFFERSIZE = 8096; // 8K is log4j default
	private static int LOGTARGET = CONSOLE; // ROLLING_FILE;

	private static String getLogsFolderPath(String fileName, String[] paths) {

		// try to obtain JWEBSOCKET_HOME environment variable
		String lWebSocketHome = System.getenv("JWEBSOCKET_HOME");
		String lFileSep = System.getProperty("file.separator");
		String lWebSocketLogs = null;

		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// logs are located in %JWEBSOCKET_HOME%/logs
			lWebSocketLogs = lWebSocketHome + "logs" + lFileSep + fileName;
		}

		if (lWebSocketLogs == null) {
			// try to obtain CATALINA_HOME environment variable
			lWebSocketHome = System.getenv("CATALINA_HOME");
			if (lWebSocketHome != null) {
				// append trailing slash if needed
				if (!lWebSocketHome.endsWith(lFileSep)) {
					lWebSocketHome += lFileSep;
				}
				// logs are located in %CATALINA_HOME%/logs
				lWebSocketLogs = lWebSocketHome + "logs" + lFileSep + fileName;
			}
		}

		return lWebSocketLogs;
	}

	// TODO: Load the conversion pattern and the logging target from a configuration file (e.g. jWebSocket.xml)
	/**
	 * Initializes the Apache log4j system to produce the desired logging output.
	 * 
	 * @param aLogLevel
	 *            one of the values TRACE, DEBUG, INFO, WARN, ERROR or FATAL.
	 * 
	 */
	private static void checkLogAppender() {
		if (LAYOUT == null) {
			LAYOUT = new PatternLayout();
			LAYOUT.setConversionPattern(pattern);
		}
		if (APPENDER == null) {
			String logsPath = getLogsFolderPath(filename, SEARCH_PATH);
			if (ROLLING_FILE == LOGTARGET && logsPath != null) {
				try {
					RollingFileAppender lRFA = new RollingFileAppender(LAYOUT, logsPath, true /* append, don't truncate */);
					lRFA.setBufferedIO(BUFFERSIZE > 0);
					lRFA.setImmediateFlush(true);
					if (BUFFERSIZE > 0) {
						lRFA.setBufferSize(BUFFERSIZE);
					}
					lRFA.setEncoding("UTF-8");
					APPENDER = lRFA;
				} catch (IOException ex) {
					APPENDER = new ConsoleAppender(LAYOUT);
				}
			} else if (SINGLE_FILE == LOGTARGET && logsPath != null) {
				try {
					FileAppender lFA = new FileAppender(LAYOUT, logsPath, true /* append, don't truncate */);
					lFA.setBufferedIO(BUFFERSIZE > 0);
					lFA.setImmediateFlush(true);
					if (BUFFERSIZE > 0) {
						lFA.setBufferSize(BUFFERSIZE);
					}
					lFA.setEncoding("UTF-8");
					APPENDER = lFA;
				} catch (IOException ex) {
					APPENDER = new ConsoleAppender(LAYOUT);
				}
			} else {
				APPENDER = new ConsoleAppender(LAYOUT);
				if (CONSOLE != LOGTARGET) {
					System.out.println("JWEBSOCKET_HOME" + " variable not set or invalid configuration," + " using console output for log file.");
				}
			}
		}

	}

	/**
	 * Initializes the jWebSocket logging system with the given log level. All subsequently instantiated class specific loggers will use this setting.
	 * 
	 * @param logLevel
	 */
	public static void initLogs(String logLevel, String logTarget, String fileName, String pattern, Integer bufferSize, String[] searchPath) {
		SEARCH_PATH = searchPath;
		if (logLevel != null) {
			LOG_LEVEL = Level.toLevel(logLevel);
		}
		if (logTarget != null) {
			if ("console".equals(logTarget)) {
				LOGTARGET = Logging.CONSOLE;
			} else if ("singlefile".equals(logTarget)) {
				LOGTARGET = Logging.SINGLE_FILE;
			} else if ("rollingfile".equals(logTarget)) {
				LOGTARGET = Logging.ROLLING_FILE;
			}
		}
		if (fileName != null) {
			filename = fileName;
		}
		if (pattern != null) {
			pattern = pattern;
		}
		if (bufferSize != null) {
			BUFFERSIZE = bufferSize;
		}
		checkLogAppender();
	}

	public static void initLogs(LoggingConfig config, String[] searchPath) {
		if (config != null) {
			initLogs(config.getLevel(), config.getAppender(), config.getFilename(), config.getPattern(), config.getBufferSize(), searchPath);
		}
	}

	/**
	 * closes the log file. Take care that no further lines are appended to the logs after it has been closed!
	 */
	public static void exitLogs() {
		if (APPENDER != null) {
			// System.out.println("Closing logs...");
			// properly close log files if such
			APPENDER.close();
			// System.out.println("Logs closed.");
		}
	}

	/**
	 * Returns a logger for a certain class by using the jWebSocket settings for logging and ignoring inherited log4j settings.
	 * 
	 * @param clz
	 * @return Logger the new logger for the given class.
	 */
	public static Logger getLogger(Class clz) {
		checkLogAppender();
		Logger logger = Logger.getLogger(clz);
		logger.addAppender(APPENDER);
		// don't inherit global log4j settings, we intend to configure that
		// in our own jWebSocket.xml config file.
		logger.setAdditivity(false);
		logger.setLevel(LOG_LEVEL);
		return logger;
	}
}
