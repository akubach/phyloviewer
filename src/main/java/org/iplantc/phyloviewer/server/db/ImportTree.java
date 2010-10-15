package org.iplantc.phyloviewer.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public class ImportTree extends ConnectionAdapter {

	PreparedStatement addTreeStmt = null;
	PreparedStatement addNodeStmt = null;
	PreparedStatement addChildStmt = null;
	
	public ImportTree(Connection conn) throws SQLException {
		super(conn);
		
		// Create our prepared statements.
		addNodeStmt = conn.prepareStatement("insert into node(Label) values (?)");
		addChildStmt = conn.prepareStatement("insert into topology (node_id, parent_id, tree_id, NumNodes, NumLeaves, Height, LeftNode, RightNode, Depth, NumChildren) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
		String sql = "insert into tree(root_id,Name) values(?, ?)";
		addTreeStmt = conn.prepareStatement(sql);
	}
	
	public void addTree(Tree tree,String name) throws SQLException
	{		
		RemoteNode root = (RemoteNode) tree.getRootNode();
		
		try
		{
			connection.setAutoCommit(false); //adding the tree in a single transaction
			
			// We need to add the root first to meet the key constraints of the database.
			addRemoteNode(root);
			
			addTreeStmt.setInt(1, tree.getRootNode().getId());
			addTreeStmt.setString(2, name != null ? name : "No name");
			addTreeStmt.execute();
			
			{
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("select currval('trees_tree_id') as result" );
				if (rs.next()) {
					tree.setId(rs.getInt("result"));
				}
			}
			
			
			addChildStmt.setInt(3, tree.getId());
			
			// Now add the children of the root.
			for (RemoteNode child : root.getChildren())
			{
				addSubtree(child, root.getId(), 1, 1, addNodeStmt, addChildStmt);
			}
			
			connection.commit();
		}
		catch(SQLException e)
		{
			//rolls back entire tree transaction on exception anywhere in the tree
			//rollback(connection);
			//e.printStackTrace();
			throw e;
		}
		finally
		{
			close(addTreeStmt);
			close(addNodeStmt);
			close(addChildStmt);
			close(connection);
		}
	}
	
	private int addSubtree(RemoteNode node, int parentID, int traversalCount, int depth, PreparedStatement addNodeStmt, PreparedStatement addChildStmt) throws SQLException 
	{
		addRemoteNode(node);
		
		int left = traversalCount;
		traversalCount++;
		
		for (RemoteNode child : node.getChildren())
		{
			traversalCount = addSubtree(child, node.getId(), traversalCount, depth + 1, addNodeStmt, addChildStmt);
		}
		
		int right = traversalCount;
		traversalCount++;
		addChild(parentID, node, left, right, depth, addChildStmt);
		
		return traversalCount;
	}

	private void addRemoteNode(RemoteNode node) throws SQLException
	{
		addNodeStmt.setString(1, node.getLabel());
		
		addNodeStmt.execute();
		
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("select currval('nodes_node_id') as result" );
		if (rs.next()) {
			node.setId(rs.getInt("result"));
		}
	}
	
	private void addChild(int parentID, RemoteNode child, int left, int right, int depth, PreparedStatement addChildStmt) throws SQLException
	{
		addChildStmt.setInt(1, child.getId());
		addChildStmt.setInt(2, parentID);
		//3 (treeID) is already set by addTree
		addChildStmt.setInt(4, child.getNumberOfNodes());
		addChildStmt.setInt(5, child.getNumberOfLeafNodes());
		addChildStmt.setInt(6, child.findMaximumDepthToLeaf());
		addChildStmt.setInt(7, left);
		addChildStmt.setInt(8, right);
		addChildStmt.setInt(9, depth);
		addChildStmt.setInt(10, child.getNumberOfChildren());
	
		addChildStmt.execute();
	}
}
