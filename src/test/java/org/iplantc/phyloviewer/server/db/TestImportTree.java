package org.iplantc.phyloviewer.server.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.iplantc.phyloviewer.client.tree.viewer.model.Node;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.shared.model.INode;
import org.junit.Before;
import org.junit.Test;

public class TestImportTree
{
	Tree tree1;
	Tree tree2;
	
	@Before
	public void setUp() throws SQLException {

		Connection conn = getConnection();
		conn.createStatement().execute(initTables);
		
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
		ImportTree it = new ImportTree(getConnection());
		it.addTree(tree1, "name0");
	
		ResultSet rs = conn.createStatement().executeQuery("select * from tree");
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("tree_id"));
		assertEquals(1, tree1.getId());
		assertEquals(1, rs.getInt("root_id"));
		assertEquals(1, tree1.getRootNode().getId());
		assertFalse(rs.next());
		
		rs = conn.createStatement().executeQuery("select * from node");
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("node_id"));
		assertFalse(rs.next());
		
		rs = conn.createStatement().executeQuery("select * from topology");
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("node_id"));
		assertEquals(0, rs.getInt("parent_id")); //getInt returns 0 for values that are null in the db
		assertEquals(1, rs.getInt("tree_id"));
		assertEquals(1, rs.getInt("LeftNode"));
		assertEquals(2, rs.getInt("RightNode"));
		assertEquals(0, rs.getInt("Depth"));
		assertEquals(0, rs.getInt("Height"));
		assertEquals(0, rs.getInt("NumChildren"));
		assertEquals(1, rs.getInt("NumLeaves"));
		assertEquals(1, rs.getInt("NumNodes"));
		assertFalse(rs.next());
		
		//tree2
		it = new ImportTree(getConnection());
		it.addTree(tree2, "name1");
		rs = conn.createStatement().executeQuery("select * from tree order by tree_id");
		assertTrue(rs.next() && rs.next());
		assertEquals(2, rs.getInt("tree_id"));
		
		INode node = tree2.getRootNode();
		rs = conn.createStatement().executeQuery("select * from topology where tree_id = 2 and node_id = " + node.getId());
		assertTrue(rs.next());
		assertEquals(node.getId(), rs.getInt("node_id"));
		assertEquals(0, rs.getInt("parent_id")); //getInt returns 0 for values that are null in the db
		assertEquals(2, rs.getInt("tree_id"));
		assertEquals(1, rs.getInt("LeftNode"));
		assertEquals(18, rs.getInt("RightNode"));
		assertEquals(0, rs.getInt("Depth"));
		assertEquals(node.findMaximumDepthToLeaf(), rs.getInt("Height"));
		assertEquals(node.getNumberOfChildren(), rs.getInt("NumChildren"));
		assertEquals(node.getNumberOfLeafNodes(), rs.getInt("NumLeaves"));
		assertEquals(node.getNumberOfNodes(), rs.getInt("NumNodes"));
		
		node = node.getChild(0);
		rs = conn.createStatement().executeQuery("select * from topology where tree_id = 2 and node_id = " + node.getId());
		assertTrue(rs.next());
		assertEquals(node.getId(), rs.getInt("node_id"));
		assertEquals(tree2.getRootNode().getId(), rs.getInt("parent_id"));
		assertEquals(2, rs.getInt("tree_id"));
		assertEquals(2, rs.getInt("LeftNode"));
		assertEquals(7, rs.getInt("RightNode"));
		assertEquals(1, rs.getInt("Depth"));
		assertEquals(node.findMaximumDepthToLeaf(), rs.getInt("Height"));
		assertEquals(node.getNumberOfChildren(), rs.getInt("NumChildren"));
		assertEquals(node.getNumberOfLeafNodes(), rs.getInt("NumLeaves"));
		assertEquals(node.getNumberOfNodes(), rs.getInt("NumNodes"));
	}
	
	private Connection getConnection() {
		try
		{
			Class.forName("org.h2.Driver");
			return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "");
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	//same as tree-data.sql, but I had to remove the "NO MAXVALUE", "NO MINVALUE" and "::regclass" for H2 to use it
	private static final String initTables = "BEGIN; CREATE SEQUENCE nodes_node_id     START WITH 1     INCREMENT BY 1  CACHE 1;  CREATE SEQUENCE trees_tree_id     START WITH 1     INCREMENT BY 1 CACHE 1; create table node (node_id integer DEFAULT nextval('nodes_node_id') primary key,  	Label varchar );  create table tree ( 	tree_id integer DEFAULT nextval('trees_tree_id') primary key,  	root_id integer not null,  	Name varchar,  	foreign key(root_id) references node(node_id) );  create table topology ( 	node_id integer primary key,  	parent_id integer,  	tree_id integer,  	LeftNode int,  	RightNode int,  	Depth int,  	Height int,  	NumChildren int,  	NumLeaves int,  	NumNodes int,  	foreign key(node_id) references node(node_id),  	foreign key(parent_id) references node(node_id),  	foreign key(tree_id) references tree(tree_id) );  create table node_layout ( 	node_id integer not null, 	layout_id varchar, 	point_x double precision, 	point_y double precision, 	min_x double precision, 	min_y double precision, 	max_x double precision, 	max_y double precision, 	foreign key(node_id) references node(node_id) );  create table overview_images ( 	tree_id integer not null, 	layout_id varchar not null, 	image_width integer not null, 	image_height integer not null, 	image_path varchar not null );  create index IndexParent on topology(parent_id); create index IndexLeft on topology(LeftNode); create index IndexRight on topology(RightNode); create index IndexDepth on topology(Depth); create index IndexTreeID on topology(tree_id);  COMMIT; "; 
}
