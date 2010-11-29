package org.iplantc.phyloviewer.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.GZIPInputStream;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.server.db.ConnectionUtil;
import org.iplantc.phyloviewer.server.db.ImportTree;
import org.iplantc.phyloviewer.server.db.ImportTreeData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDatabaseTreeData
{
	static final String DB = "testdb";
	static DataSource pool;
	static DatabaseTreeData treeData;
	static RemoteNode child0;
	static RemoteNode child1;
	static RemoteNode parent;
	static Tree tree;

	@BeforeClass
	public static void classSetUp() throws ClassNotFoundException, SQLException {
		{
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql:phyloviewer", "phyloviewer", "phyloviewer");
			conn.createStatement().execute("DROP DATABASE IF EXISTS " + DB);
			conn.createStatement().execute("CREATE DATABASE " + DB + " WITH TEMPLATE phyloviewer_template;");
			conn.close();
		}
		
		{
			pool = new MockDataSource();
			treeData = new DatabaseTreeData(pool);
			
			child0 = new RemoteNode(0, "", 0, 1, 1, 1, 0, 2, 3); //note: IDs will be changed when the node is inserted into the DB
			child1 = new RemoteNode(1, "", 0, 1, 1, 1, 0, 4, 5);
			RemoteNode[] children = new RemoteNode[] { child0, child1 };
			parent = new RemoteNode(2, "", 2, 3, 2, 0, 1, 1, 6);
			parent.setChildren(children);
	
			tree = new Tree();
			tree.setId(0);
			tree.setRootNode(parent);
			
			Connection conn2 = pool.getConnection();
			ImportTree it = new ImportTree(conn2);
			it.addTree(tree,"");
			it.close();
			conn2.close();
		}
	}
	
	@AfterClass
	public static void classTearDown() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:postgresql:phyloviewer", "phyloviewer", "phyloviewer");
		conn.createStatement().execute("DROP DATABASE IF EXISTS " + DB);
		conn.close();
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

	@Test
	public void testGetChildren()
	{
		RemoteNode[] returnedChildren = treeData.getChildren(parent.getId());
		assertNotNull(returnedChildren);
		assertEquals(2, returnedChildren.length);
		assertTrue(arrayContains(child0, returnedChildren));
		assertTrue(arrayContains(child1, returnedChildren));

		returnedChildren = treeData.getChildren(child0.getId());
		assertNull(returnedChildren);

		returnedChildren = treeData.getChildren(child1.getId());
		assertNull(returnedChildren);
	}

	private <T> boolean arrayContains(T obj, T[] array)
	{
		for (T element : array)
		{
			if (element.equals(obj)){
				return true;
			}
		}
		
		return false;
	}

	@Test
	public void testGetTree()
	{
		Tree returnedTree = treeData.getTree(tree.getId(), 1);
		assertEquals(tree, returnedTree);
	}
	
	@Test
	public void testGetSubtree() {
		RemoteNode subtree = treeData.getSubtree(parent.getId(), 0);
		assertNull(subtree.getChildren());
		assertEquals(2, subtree.getNumberOfChildren());
		
		subtree = treeData.getSubtree(parent.getId(), 1);
		assertTrue(arrayContains(child0, subtree.getChildren()));
		assertTrue(arrayContains(child1, subtree.getChildren()));
		assertNull(subtree.getChild(0).getChildren());
		assertEquals(0, subtree.getChild(0).getNumberOfChildren());
		assertNull(subtree.getChild(1).getChildren());
		assertEquals(0, subtree.getChild(1).getNumberOfChildren());
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
//		classSetUp(); //creates new empty testdb database
//		Tree tree = loadBenchmarkTree(); //loads NCBI tree
		
		MockDataSource pool = new MockDataSource();
		DatabaseTreeData treeData = new DatabaseTreeData(pool);
		
		Connection connection = pool.getConnection();
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("select * from tree where name = 'ncbi'");
		rs.next();
		int rootID = rs.getInt("root_id");
		ConnectionUtil.close(rs);
		ConnectionUtil.close(statement);
		ConnectionUtil.close(connection);
		
		System.out.println("depth\tnodes\t2Q\tRec");
		for (int depth = 1; depth <= 10; depth += 1) 
		{
			
			System.gc();
			long t00 = System.currentTimeMillis();
			RemoteNode subtree = treeData.getSubtreeInTwoQueries(rootID, depth);
			long t01 = System.currentTimeMillis();
			
			System.gc();
			long t10 = System.currentTimeMillis();
			treeData.getSubtreeRecursive(rootID, depth);
			long t11 = System.currentTimeMillis();
			
			int nodeCount = subtree.getNumberOfLocalNodes();
			
			System.out.println(depth + "\t" + nodeCount + "\t" + (t01 - t00) + "\t" + (t11 - t10));
		}
	}
	
	private Tree loadBenchmarkTree() throws FileNotFoundException, IOException, SQLException {
		File file = new File("./src/test/resources/data/ncbi-taxonomy.nwk.gz");
		BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
		StringBuffer newick = new StringBuffer();
		
		String line;
		while((line = in.readLine()) != null)
		{
			newick.append(line);
		}

		RemoteNode root = ImportTreeData.rootNodeFromNewick(newick.toString(), "ncbi");

		Tree tree = new Tree();
		tree.setRootNode(root);
		
		Connection conn = pool.getConnection();
		conn.setAutoCommit(false);
		ImportTree it = new ImportTree(conn);
		it.addTree(tree, "ncbi");
		conn.commit();
		it.close();
		conn.close();
		
		return tree;
	}
	
	private static class MockDataSource implements DataSource {

		public MockDataSource() throws SQLException, ClassNotFoundException {
			Class.forName("org.postgresql.Driver");
		}

		@Override
		public Connection getConnection() throws SQLException
		{
			return DriverManager.getConnection("jdbc:postgresql:" + DB, "phyloviewer", "phyloviewer");
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException
		{
			return DriverManager.getConnection("jdbc:postgresql:" + DB, "phyloviewer", "phyloviewer");
		}

		@Override public PrintWriter getLogWriter() throws SQLException { return null; }
		@Override public int getLoginTimeout() throws SQLException { return 0; }
		@Override public void setLogWriter(PrintWriter out) throws SQLException {}
		@Override public void setLoginTimeout(int seconds) throws SQLException {}
		@Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
		@Override public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
	}
}
