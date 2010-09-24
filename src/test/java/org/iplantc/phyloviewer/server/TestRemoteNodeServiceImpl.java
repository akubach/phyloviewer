package org.iplantc.phyloviewer.server;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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
	public void testFetchTree() throws ServletException {
		ServletConfig config = new MockServletConfig();
		
		RemoteNodeServiceImpl impl = new RemoteNodeServiceImpl();
		impl.init(config);
		
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
		
		RemoteNode child0 = new RemoteNode(UUID.uuid(), "bar", 1, 0, new RemoteNode[0]);
		RemoteNode child1 = new RemoteNode(UUID.uuid(), "baz", 1, 0, new RemoteNode[0]);
		RemoteNode[] children = new RemoteNode[] {child0, child1};
		RemoteNode parent = new RemoteNode(UUID.uuid(), "foo", 2, 1, children);

		impl.addRemoteNode(parent);
		
		RemoteNode[] fetchedChildren = impl.getChildren(parent.getUUID());
		assertEquals(children, fetchedChildren);
	}
	
	@SuppressWarnings("unchecked")
	private class MockServletConfig implements ServletConfig {
		ServletContext context = new MockServletContext();

		@Override
		public ServletContext getServletContext() {
			return context;
		}
		
		@Override public String getInitParameter(String arg0) {return null;}
		@Override public Enumeration getInitParameterNames() {return null;}
		@Override public String getServletName() { return null; }
	}
	
	@SuppressWarnings("unchecked")
	private class MockServletContext implements ServletContext {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		
		@Override
		public Object getAttribute(String arg0) {
			return attributes.get(arg0);
		}
		
		@Override
		public void setAttribute(String arg0, Object arg1) {
			attributes.put(arg0, arg1);
		}

		@Override public Enumeration getAttributeNames() {return null;}
		@Override public ServletContext getContext(String arg0) {return null;}
		@Override public String getContextPath() {return null;}
		@Override public String getInitParameter(String arg0) {return null;}
		@Override public Enumeration getInitParameterNames() {return null;}
		@Override public int getMajorVersion() {return 0;}
		@Override public String getMimeType(String arg0) {return null;}
		@Override public int getMinorVersion() {return 0;}
		@Override public RequestDispatcher getNamedDispatcher(String arg0) {return null;}
		@Override public String getRealPath(String arg0) {return null;}
		@Override public RequestDispatcher getRequestDispatcher(String arg0) {return null;}
		@Override public URL getResource(String arg0) throws MalformedURLException {return null;}
		@Override public InputStream getResourceAsStream(String arg0) {return null;}
		@Override public Set getResourcePaths(String arg0) {return null;}
		@Override public String getServerInfo() {return null;}
		@Override public Servlet getServlet(String arg0) throws ServletException {return null;}
		@Override public String getServletContextName() {return null;}
		@Override public Enumeration getServletNames() {return null;}
		@Override public Enumeration getServlets() {return null;}
		@Override public void log(String arg0){}
		@Override public void log(Exception arg0, String arg1){}
		@Override public void log(String arg0, Throwable arg1){}
		@Override public void removeAttribute(String arg0){}
	}
}
