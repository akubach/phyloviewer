package org.iplantc.phyloviewer.server.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Node;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.viewer.server.db.ImportTree;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestImportTree
{
	static final String DB = "testdb";
	Tree tree1;
	Tree tree2;
	
	@BeforeClass
	public static void classSetUp() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection("jdbc:postgresql:phyloviewer", "phyloviewer", "phyloviewer");
		conn.createStatement().execute("DROP DATABASE IF EXISTS " + DB);
		conn.createStatement().execute("CREATE DATABASE " + DB + " WITH TEMPLATE phyloviewer_template;");
		//FIXME copies existing data from phyloviewer db
	}
	
	@AfterClass
	public static void classTearDown() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:postgresql:phyloviewer", "phyloviewer", "phyloviewer");
		conn.createStatement().execute("DROP DATABASE IF EXISTS " + DB);
	}
	
	@Before
	public void setUp() throws SQLException {

		Node root0 = new Node(null);
		tree1 = new Tree();
		tree1.setId(0);
		tree1.setRootNode(root0);
		
		Node root1 = new Node(new Node[] {
			new Node(new Node[] {
				new Node(), 
				new Node()
			}), 
			new Node(new Node[] {
				new Node(new Node[] {
					new Node(), 
					new Node()
				}),
				new Node()
			})
		});
		tree2 = new Tree();
		tree2.setId(1);
		tree2.setRootNode(root1);
	}
	
	@Test
	public void testAddTree() throws SQLException
	{
		Connection conn = getConnection();
		
		//tree1
		INode node = tree1.getRootNode();
		ImportTree it = new ImportTree(conn);
		it.addTree(tree1, "name1");
		it.close();
	
		ResultSet rs = conn.createStatement().executeQuery("select * from tree");
		assertTrue(rs.next());
		assertEquals(tree1.getId(), rs.getInt("tree_id"));
		assertEquals(node.getId(), rs.getInt("root_id"));
		assertFalse(rs.next());
		
		rs = conn.createStatement().executeQuery("select * from node");
		assertTrue(rs.next());
		assertEquals(node.getId(), rs.getInt("node_id"));
		assertFalse(rs.next());
		
		rs = conn.createStatement().executeQuery("select * from topology");
		assertTrue(rs.next());
		assertEquals(node.getId(), rs.getInt("node_id"));
		assertEquals(0, rs.getInt("parent_id")); //getInt returns 0 for values that are null in the db
		assertTrue(rs.wasNull());
		assertEquals(tree1.getId(), rs.getInt("tree_id"));
		assertEquals(1, rs.getInt("LeftNode"));
		assertEquals(2, rs.getInt("RightNode"));
		assertEquals(0, rs.getInt("Depth"));
		assertEquals(0, rs.getInt("Height"));
		assertEquals(0, rs.getInt("NumChildren"));
		assertEquals(1, rs.getInt("NumLeaves"));
		assertEquals(1, rs.getInt("NumNodes"));
		assertFalse(rs.next());
		
		//tree2
		it = new ImportTree(conn);
		it.addTree(tree2, "name2");
		it.close();
		
		rs = conn.createStatement().executeQuery("select * from tree order by tree_id");
		assertTrue(rs.next() && rs.next());
		assertEquals(tree2.getId(), rs.getInt("tree_id"));
		
		node = tree2.getRootNode();
		rs = conn.createStatement().executeQuery("select * from topology where tree_id = " + tree2.getId() + " and node_id = " + node.getId());
		assertTrue(rs.next());
		assertEquals(node.getId(), rs.getInt("node_id"));
		assertEquals(0, rs.getInt("parent_id")); //getInt returns 0 for values that are null in the db
		assertEquals(tree2.getId(), rs.getInt("tree_id"));
		assertEquals(1, rs.getInt("LeftNode"));
		assertEquals(18, rs.getInt("RightNode"));
		assertEquals(0, rs.getInt("Depth"));
		assertEquals(node.findMaximumDepthToLeaf(), rs.getInt("Height"));
		assertEquals(node.getNumberOfChildren(), rs.getInt("NumChildren"));
		assertEquals(node.getNumberOfLeafNodes(), rs.getInt("NumLeaves"));
		assertEquals(node.getNumberOfNodes(), rs.getInt("NumNodes"));
		
		node = node.getChild(0);
		rs = conn.createStatement().executeQuery("select * from topology where tree_id = " + tree2.getId() + " and node_id = " + node.getId());
		assertTrue(rs.next());
		assertEquals(node.getId(), rs.getInt("node_id"));
		assertEquals(tree2.getRootNode().getId(), rs.getInt("parent_id"));
		assertEquals(tree2.getId(), rs.getInt("tree_id"));
		assertEquals(2, rs.getInt("LeftNode"));
		assertEquals(7, rs.getInt("RightNode"));
		assertEquals(1, rs.getInt("Depth"));
		assertEquals(node.findMaximumDepthToLeaf(), rs.getInt("Height"));
		assertEquals(node.getNumberOfChildren(), rs.getInt("NumChildren"));
		assertEquals(node.getNumberOfLeafNodes(), rs.getInt("NumLeaves"));
		assertEquals(node.getNumberOfNodes(), rs.getInt("NumNodes"));
		
		conn.close();
	}
	
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:postgresql:" + DB, "phyloviewer", "phyloviewer");
	}
}
