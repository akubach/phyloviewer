package org.iplantc.phyloviewer.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.model.INode;

public class ImportTree {

	Connection connection;
	PreparedStatement addTreeStmt = null;
	PreparedStatement addNodeStmt = null;
	PreparedStatement addChildStmt = null;
	PreparedStatement getNodeIdStmt = null;
	
	public ImportTree(Connection conn) throws SQLException {
		this.connection = conn;
	
		// Create our prepared statements.
		addNodeStmt = conn.prepareStatement("insert into node(Label) values (?)");
		addChildStmt = conn.prepareStatement("insert into topology (node_id, parent_id, tree_id, NumNodes, NumLeaves, Height, LeftNode, RightNode, Depth, NumChildren) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
		String sql = "insert into tree(root_id,Name) values(?, ?)";
		addTreeStmt = conn.prepareStatement(sql);
		
		getNodeIdStmt = conn.prepareStatement("select currval('nodes_node_id') as result"); //TODO: I think PreparedStatement can return the sequence number without a query, using Statement.RETURN_GENERATED_KEYS
	}
	
	public void close() {
		ConnectionAdapter.close(addTreeStmt);
		ConnectionAdapter.close(addNodeStmt);
		ConnectionAdapter.close(addChildStmt);
		ConnectionAdapter.close(getNodeIdStmt);
	}
	
	public void addTree(Tree tree,String name) throws SQLException
	{		
		INode root = tree.getRootNode();
		
		try
		{
			// We need to add the root first to meet the key constraints of the database.
			addNode(root);
			
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
			

			if (root instanceof RemoteNode)
			{
				//add the tree root to the topology table
				addChild(null, (RemoteNode) root, addChildStmt);
				
				// add the children of the root.
				if (root.getChildren() != null)
				{
					for (RemoteNode child : (RemoteNode[]) root.getChildren())
					{
						addSubtree(child, root.getId(), addNodeStmt, addChildStmt);
					}
				}
			}
			else
			{
				//if not RemoteNode, the depth and left and right indices have to be calculated in the traversal
				int left = 1;
				int right = 2 * root.getNumberOfNodes();
				int depth = 0;
				addChild(null, root, left, right, depth, addChildStmt);
				
				// add the children of the root.
				if (root.getChildren() != null)
				{
					for (INode child : root.getChildren())
					{
						addSubtree(child, root.getId(), left + 1, depth + 1, addNodeStmt, addChildStmt);
					}
				}
			}
		}
		catch(SQLException e)
		{
			// Rethrow.
			throw e;
		}
		finally
		{
			this.close();
		}
	}
	
	private int addSubtree(INode node, int parentID, int traversalCount, int depth, PreparedStatement addNodeStmt, PreparedStatement addChildStmt) throws SQLException 
	{
		addNode(node);
		
		int left = traversalCount;
		traversalCount++;
		
		if (node.getChildren() != null)
		{
			for (INode child : node.getChildren())
			{
				traversalCount = addSubtree(child, node.getId(), traversalCount, depth + 1, addNodeStmt, addChildStmt);
			}
		}
		
		int right = traversalCount;
		traversalCount++;
		addChild(parentID, node, left, right, depth, addChildStmt);
		
		return traversalCount;
	}
	
	private void addSubtree(RemoteNode node, int parentID, PreparedStatement addNodeStmt, PreparedStatement addChildStmt) throws SQLException
	{
		addNode(node);
		
		if (node.getChildren() != null)
		{
			for (RemoteNode child : node.getChildren())
			{
				addSubtree(child, node.getId(), addNodeStmt, addChildStmt);
			}
		}
		
		addChild(parentID, node, addChildStmt);
	}

	private void addNode(INode node) throws SQLException
	{
		addNodeStmt.setString(1, node.getLabel());
		addNodeStmt.execute();
		
		ResultSet rs = getNodeIdStmt.executeQuery();
		if (rs.next()) {
			node.setId(rs.getInt("result"));
		}
	}
	
	private void addChild(Integer parentID, INode child, int left, int right, int depth, PreparedStatement addChildStmt) throws SQLException
	{
		addChildStmt.setInt(1, child.getId());
		
		if (parentID != null)
		{
			addChildStmt.setInt(2, parentID);
		}
		else
		{
			addChildStmt.setNull(2, Types.INTEGER);
		}
		
		//param 3 (treeID) is already set by addTree
		addChildStmt.setInt(4, child.getNumberOfNodes());
		addChildStmt.setInt(5, child.getNumberOfLeafNodes());
		addChildStmt.setInt(6, child.findMaximumDepthToLeaf());
		addChildStmt.setInt(7, left);
		addChildStmt.setInt(8, right);
		addChildStmt.setInt(9, depth);
		addChildStmt.setInt(10, child.getNumberOfChildren());
	
		addChildStmt.execute();
	}
	
	private void addChild(Integer parentID, RemoteNode child, PreparedStatement addChildStmt) throws SQLException
	{
		addChildStmt.setInt(1, child.getId());
		
		if (parentID != null)
		{
			addChildStmt.setInt(2, parentID);
		}
		else
		{
			addChildStmt.setNull(2, Types.INTEGER);
		}
		
		//param 3 (treeID) is already set by addTree
		addChildStmt.setInt(4, child.getNumberOfNodes());
		addChildStmt.setInt(5, child.getNumberOfLeafNodes());
		addChildStmt.setInt(6, child.findMaximumDepthToLeaf());
		addChildStmt.setInt(7, child.getLeftIndex());
		addChildStmt.setInt(8, child.getRightIndex());
		addChildStmt.setInt(9, child.getDepth());
		addChildStmt.setInt(10, child.getNumberOfChildren());
	
		addChildStmt.execute();
	}
}
