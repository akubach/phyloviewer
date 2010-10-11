package org.iplantc.phyloviewer.server;

import junit.framework.TestCase;

import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Tests RemoteNodeService servlet implementation.  (Just a JUnit test.  Runs faster than GwtTestRemoteNodeService).
 */
public class TestRemoteNodeServiceImpl extends TestCase {
		
	@Test 
	public void testMapSubtree() throws JSONException {
		
		String json = "{\"name\":\"foo\"}";
		JSONObject obj = new JSONObject(json);
		RemoteNode node = RemoteNodeServiceImpl.mapSubtree(obj);

		assertEquals("foo", node.getLabel());
		assertEquals(1, node.getNumberOfLeafNodes());
		assertEquals(0, node.findMaximumDepthToLeaf());
		assertEquals(0, node.getNumberOfChildren());
		assertEquals(0, node.getChildren().length);
		assertTrue(node.isLeaf());

		json = "{\"children\":[" + "{\"name\":\"bar\"}," + "{\"name\":\"baz\"}" + "]}";
		obj = new JSONObject(json);
		node = RemoteNodeServiceImpl.mapSubtree(obj);

		assertEquals("bar", node.getLabel()); // parent takes first child's label
		assertEquals(2, node.getNumberOfLeafNodes());
		assertEquals(1, node.findMaximumDepthToLeaf());
		assertEquals(2, node.getNumberOfChildren());
		assertEquals(2, node.getChildren().length);
		assertFalse(node.isLeaf());
	}
}
