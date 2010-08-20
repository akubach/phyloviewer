package org.iplantc.phyloviewer.server;

import junit.framework.TestCase;

import org.iplantc.phyloviewer.client.FetchTree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Tests RemoteNodeService servlet implementation.  (Just a JUnit test.  Runs faster than GwtTestRemoteNodeService).
 */
public class TestRemoteNodeServiceImpl extends TestCase {
	Tree tree;
	RemoteNode root;
	
	@Test 
	public void testMapSubtree() throws JSONException {
		RemoteNodeServiceImpl impl = new RemoteNodeServiceImpl();
		String json = "{\"name\":\"foo\"}";
		JSONObject obj = new JSONObject(json);
		RemoteNode node = impl.mapSubtree(obj);
		
		assertEquals("foo", node.getLabel());
		assertEquals(1, node.getNumberOfLeafNodes());
		assertEquals(0, node.findMaximumDepthToLeaf());
		assertEquals(0, node.getNumberOfChildren());
		assertEquals(0, node.getChildren().length);
		assertEquals(node.getChildren().length, impl.getChildren(node.getUUID()).length); 
		assertTrue(node.isLeaf());
		
		json = "{\"children\":[" +
					"{\"name\":\"bar\"}," +
					"{\"name\":\"baz\"}" +
				"]}";
		obj = new JSONObject(json);
		node = impl.mapSubtree(obj);
		
		assertEquals("bar", node.getLabel()); //parent takes first child's label
		assertEquals(2, node.getNumberOfLeafNodes());
		assertEquals(1, node.findMaximumDepthToLeaf());
		assertEquals(2, node.getNumberOfChildren());
		assertEquals(2, node.getChildren().length);
		assertEquals(node.getChildren().length, impl.getChildren(node.getUUID()).length); 
		assertFalse(node.isLeaf());
	}
	
	@Test
	public void testFetchTree() {
		RemoteNodeServiceImpl impl = new RemoteNodeServiceImpl();
		final String json = "{\"root\":{\"name\":\"foo\",\"children\":[" +
			"{\"name\":\"bar\"}," +
			"{\"name\":\"baz\"}" +
		"]}}";
		
		FetchTree mockFetchTree = new FetchTree() {
			public String fetchTree(int tree) { return json; }
		};
		
		impl.setFetchTree(mockFetchTree);
		
		Tree tree = impl.fetchTree(0);
		assertNotNull(tree);
		assertEquals(3, tree.getNumberOfNodes());
		RemoteNode root = (RemoteNode) tree.getRootNode();
		assertNotNull(root);
		assertNotNull(root.getChildren());
		assertEquals(1, root.findMaximumDepthToLeaf());
		assertEquals(2, root.getChildren().length);
	}
	
	@Test
	public void testGetChildren() {
		RemoteNodeServiceImpl impl = new RemoteNodeServiceImpl();
		RemoteNode parent = new RemoteNode(UUID.uuid(), "foo", 2, 1);
		RemoteNode child0 = new RemoteNode(UUID.uuid(), "bar", 1, 0);
		RemoteNode child1 = new RemoteNode(UUID.uuid(), "baz", 1, 0);
		RemoteNode[] children = new RemoteNode[] {child0, child1};
		parent.setChildren(children);
		impl.addRemoteNode(parent);
		
		RemoteNode[] fetchedChildren = impl.getChildren(parent.getUUID());
		assertEquals(children, fetchedChildren);
	}
}
