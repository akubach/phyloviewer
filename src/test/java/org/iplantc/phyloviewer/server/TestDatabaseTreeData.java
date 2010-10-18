package org.iplantc.phyloviewer.server;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.DemoTree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.json.JSONObject;
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
		
		/*child0 = new RemoteNode(0,"", 1, 1, 0, new RemoteNode[0]);
		child1 = new RemoteNode(1,"", 1, 1, 0, new RemoteNode[0]);
		RemoteNode[] children = new RemoteNode[] { child0, child1 };
		parent = new RemoteNode(2, "", 3, 2, 1, children);

		tree = new Tree();
		tree.setId(0);
		tree.setRootNode(parent);
		treeData.addTree(tree,"");*/
	}
	
	@Test
	public void testGetRemoteNode() {
		RemoteNode returned = treeData.getSubtree(parent.getId(),1);
		assertNotNull(returned);
		assertEquals(parent.getNumberOfChildren(), returned.getNumberOfChildren());
		assertEquals(parent.getNumberOfLeafNodes(), returned.getNumberOfLeafNodes());
		assertEquals(parent.getNumberOfNodes(), returned.getNumberOfNodes());
		assertEquals(parent.findMaximumDepthToLeaf(), returned.findMaximumDepthToLeaf());
	}

	/*@Test
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
	}*/
	
	/*@Test
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
	}*/
	
	/*public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DemoTree demoTree = DemoTree.NCBI_TAXONOMY;
		
		JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "");
		DatabaseTreeData treeData = new DatabaseTreeData(pool);
		
		Tree tree;

		File file = new File("./war", demoTree.localPath);
		String json = FetchTreeImpl.readFile(file);

		JSONObject root = LoadTreeData.parseTree(json);

		RemoteNode remoteRoot = LoadTreeData.mapSubtree(root);
		tree = new Tree();
		tree.setId(demoTree.id);
		tree.setRootNode(remoteRoot);
		
		treeData.addTree(tree,"");
		
		System.out.println("depth\tnodes\t2Q\tRec");
		for (int depth = 5; depth <= 30; depth += 5) 
		{
			
			System.gc();
			long t00 = System.currentTimeMillis();
			RemoteNode subtree = treeData.getSubtreeInTwoQueries(tree.getRootNode().getUUID(), depth);
			long t01 = System.currentTimeMillis();
			
			System.gc();
			long t10 = System.currentTimeMillis();
			treeData.getSubtreeRecursive(tree.getRootNode().getUUID(), depth);
			long t11 = System.currentTimeMillis();
			
			int nodeCount = subtree.getNumberOfLocalNodes();
			
			System.out.println(depth + "\t" + nodeCount + "\t" + (t01 - t00) + "\t" + (t11 - t10));
		}
	}*/
	
	private static class MockDataSource implements DataSource {

		public MockDataSource() throws SQLException, ClassNotFoundException {
			Class.forName("org.h2.Driver");
		}

		@Override
		public Connection getConnection() throws SQLException
		{
			return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "");
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException
		{
			return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "");
		}

		@Override public PrintWriter getLogWriter() throws SQLException { return null; }
		@Override public int getLoginTimeout() throws SQLException { return 0; }
		@Override public void setLogWriter(PrintWriter out) throws SQLException {}
		@Override public void setLoginTimeout(int seconds) throws SQLException {}
		@Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
		@Override public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
	}

}
