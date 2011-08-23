package net.ion.websocket.common;

import java.util.ArrayList;
import java.util.List;

public class MySessionManager {

	private static List<String> userList = new ArrayList<String>();
	
	public static void addSession(String username) {
		if (!isSessionUser(username)) userList.add(username);
	}

	public static void removeSession(String username) {
		userList.remove(username);
	}

	public static String[] toArray() {
		return userList.toArray(new String[0]);
	}

	public static boolean isSessionUser(String username) {
		return userList.contains(username);
	}

}
