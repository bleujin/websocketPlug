package net.ion.websocket.server;

import java.util.Map;

import junit.framework.TestCase;

public class TestURIParser extends TestCase {

	public void testBlank() throws Exception {
		Map<String, String> map = URIParser.parse("/;timeout=3600000", "");
		assertEquals(0, map.size());
	}


	public void testLimit1() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/", "/{userId}/{sessionId}/{params}");

		assertEquals(1, map.size());
		assertEquals("bleujin", map.get("userId"));
	}

	public void testCaseSensitive() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/", "/{userId}/{sessionId}/{params}");

		assertEquals(1, map.size());
		assertNull(map.get("userid"));
		assertNotNull(map.get("userId"));
	}

	public void testLimit2() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/123", "/{userId}/{sessionId}/{params}");

		assertEquals(2, map.size());
		assertEquals("bleujin", map.get("userId"));
		assertEquals("123", map.get("sessionId"));
	}

	public void testLimitCase() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/", "/{userId}/{options}");

		assertEquals(1, map.size());
		assertEquals("bleujin", map.get("userId"));
		assertTrue(map.get("options") == null);
	}

	public void testPattern() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/123/greet=hello&dd=444", "/{userId}/{sessionId}/{params}");

		assertEquals(3, map.size());
		assertEquals("bleujin", map.get("userId"));
		assertEquals("greet=hello&dd=444", "greet=hello&dd=444");
	}

	public void testOverCase() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/1234", "/{userId}");

		assertEquals(1, map.size());
		assertEquals("bleujin", map.get("userId"));
	}

	

}
