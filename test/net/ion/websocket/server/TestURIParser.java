package net.ion.websocket.server;

import java.util.Map;

import junit.framework.TestCase;

public class TestURIParser extends TestCase {

	public void testBlankCase() throws Exception {
		Map<String, String> map = URIParser.parse("/;timeout=3600000", "");
		assertEquals(0, map.size());
	}

	public void testFixCase() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/123", "/{userId}/{sessionId}/{params}");

		assertEquals(2, map.size());
		assertEquals("bleujin", map.get("userId"));
		assertEquals("123", map.get("sessionId"));
	}

	public void testPattern2() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/123/greet=hello&dd=444", "/{userId}/{sessionId}/{params}");

		assertEquals(3, map.size());
		assertEquals("bleujin", map.get("userId"));
		assertEquals("greet=hello&dd=444", "greet=hello&dd=444");
	}

	public void testOptionCase() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/", "/{userId}/{options}");

		assertEquals(1, map.size());
		assertEquals("bleujin", map.get("userId"));
		assertTrue(map.get("options") == null);
	}

	public void testConfig2() throws Exception {
		Map<String, String> map = URIParser.parse("/bleujin/", "/{userId}/{sessionId};{params}");

		assertEquals(1, map.size());
		assertEquals("bleujin", map.get("userId"));
	}


}
