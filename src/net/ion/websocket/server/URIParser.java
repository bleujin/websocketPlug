package net.ion.websocket.server;

import java.util.Collections;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;

public class URIParser {

	static String FIND_PATTERN =  "\\{[^/]+\\}";
	static String TRANS_PATTERN = "[^/\\?]+?";
	static String SPLIT_CHAR = "/";
	
	public static Map<String, String> parse(String _url, String _pattern) {
		String[] urls = StringUtil.split(_url, SPLIT_CHAR);
		String[] urlPatterns = StringUtil.split(_pattern, SPLIT_CHAR);

		if (urlPatterns.length == 0) return Collections.EMPTY_MAP ; 
		
		Map<String, String> result = MapUtil.newMap();
		for (int i = 0; i < urls.length; i++) {
			if (i > urlPatterns.length-1) continue ;
			String pattern = urlPatterns[i];
			String url = urls[i];
			if (pattern.matches(FIND_PATTERN)) {
				result.put(pattern.substring(1, pattern.length() - 1), url);
			}
		}
		return result;
	}

}
