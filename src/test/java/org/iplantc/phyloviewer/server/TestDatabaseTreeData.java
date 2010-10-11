package org.iplantc.phyloviewer.server;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.junit.Before;
import org.junit.Test;

public class TestDatabaseTreeData
{
	DatabaseTreeData treeData;
	RemoteNode child0;
	RemoteNode child1;
	RemoteNode parent;
	Tree tree;

	@Before
	public void setUp() throws SQLException, ClassNotFoundException
	{
		DataSource pool = new MockDataSource();
		treeData = new DatabaseTreeData(pool);
		
		child0 = new RemoteNode(UUID.randomUUID().toString(), "", 1, 1, 0, new RemoteNode[0]);
		child1 = new RemoteNode(UUID.randomUUID().toString(), "", 1, 1, 0, new RemoteNode[0]);
		RemoteNode[] children = new RemoteNode[] { child0, child1 };
		parent = new RemoteNode(UUID.randomUUID().toString(), "", 3, 2, 1, children);

		tree = new Tree();
		tree.setId(UUID.randomUUID().toString());
		tree.setRootNode(parent);
		treeData.addTree(tree);
	}
	
	@Test
	public void testGetRemoteNode() {
		RemoteNode returned = treeData.getSubtree(parent.getUUID(),1);
		assertNotNull(returned);
		assertEquals(parent.getNumberOfChildren(), returned.getNumberOfChildren());
		assertEquals(parent.getNumberOfLeafNodes(), returned.getNumberOfLeafNodes());
		assertEquals(parent.getNumberOfNodes(), returned.getNumberOfNodes());
		assertEquals(parent.findMaximumDepthToLeaf(), returned.findMaximumDepthToLeaf());
	}

	@Test
	public void testGetChildren()
	{
		RemoteNode[] returnedChildren = treeData.getChildren(parent.getUUID());
		assertNotNull(returnedChildren);
		assertEquals(2, returnedChildren.length);
		assertEquals(child0, returnedChildren[0]);
		assertEquals(child1, returnedChildren[1]);

		returnedChildren = treeData.getChildren(child0.getUUID());
		assertNull(returnedChildren);

		returnedChildren = treeData.getChildren(child1.getUUID());
		assertNull(returnedChildren);
	}

	@Test
	public void testGetTree()
	{
		Tree returnedTree = treeData.getTree(tree.getId(), 1);
		assertEquals(tree, returnedTree);
	}
	
	@Test
	public void testGetSubtree() {
		RemoteNode subtree = treeData.getSubtree(parent.getUUID(), 0);
		assertNull(subtree.getChildren());
		assertEquals(2, subtree.getNumberOfChildren());
		
		subtree = treeData.getSubtree(parent.getUUID(), 1);
		assertArrayEquals(new RemoteNode[] {child0, child1}, subtree.getChildren()); //note: the sibling order isn't really guaranteed by the database right now, so this may fail.
		assertNull(subtree.getChild(0).getChildren());
		assertEquals(0, subtree.getChild(0).getNumberOfChildren());
		assertNull(subtree.getChild(1).getChildren());
		assertEquals(0, subtree.getChild(1).getNumberOfChildren());
	}
	
	private class MockDataSource implements DataSource {

		public MockDataSource() throws SQLException, ClassNotFoundException {
			Class.forName("org.h2.Driver");
		}

		@Override
		public Connection getConnection() throws SQLException
		{
			return DriverManager.getConnection("jdbc:h2:tcp://localhost/mem:testdb;DB_CLOSE_DELAY=-1", "", "");
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException
		{
			return DriverManager.getConnection("jdbc:h2:tcp://localhost/mem:testdb;DB_CLOSE_DELAY=-1", "", "");
		}

		@Override public PrintWriter getLogWriter() throws SQLException { return null; }
		@Override public int getLoginTimeout() throws SQLException { return 0; }
		@Override public void setLogWriter(PrintWriter out) throws SQLException {}
		@Override public void setLoginTimeout(int seconds) throws SQLException {}
		@Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
		@Override public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
	}

}
