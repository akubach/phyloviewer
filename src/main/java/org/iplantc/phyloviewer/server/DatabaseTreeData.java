package org.iplantc.phyloviewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.server.db.ConnectionAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseTreeData implements ITreeData

{
	public static final int SUBTREE_QUERY_THRESHOLD = 4; //recalculated for NCBI tree on postgres database 
	
	private DataSource pool;

	public DatabaseTreeData(DataSource pool) {
		this.pool = pool;
	}
	
	@Override
	public RemoteNode getSubtree(int rootID, int depth)
	{
		if (depth >= SUBTREE_QUERY_THRESHOLD) 
		{
			return getSubtreeInTwoQueries(rootID, depth);
		} 
		else 
		{
			return getSubtreeRecursive(rootID, depth);
		}
		
	}
	
	public RemoteNode getSubtreeRecursive(int rootID, int depth)
	{
		RemoteNode node = null;
		Connection conn = null;
		PreparedStatement rootNodeStmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = pool.getConnection();
			rootNodeStmt = conn.prepareStatement("select * from node natural join topology where node.node_id = ?");
			
			rootNodeStmt.setInt(1, rootID);
			
			rs = rootNodeStmt.executeQuery();
			
			if (rs.next()) 
			{
				node = createNode(rs);
			
				if (depth > 0 && node.getNumberOfChildren() > 0) {
					RemoteNode[] children = getChildren(node.getId(), depth - 1, conn);
					node.setChildren(children);
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectionAdapter.close(rootNodeStmt);
			ConnectionAdapter.close(rs);
			ConnectionAdapter.close(conn);
		}
		
		return node;
	}

	public RemoteNode getSubtreeInTwoQueries(int rootID, int depth)
	{
		RemoteNode subtree = null;
		Connection conn = null;
		PreparedStatement getRoot = null;
		PreparedStatement getSubtree = null;
		ResultSet rootRS = null;
		ResultSet subtreeRS = null;
		
		try
		{
			//using some extra topology metadata to get the entire subtree in two queries instead of recursively querying for children
			conn = pool.getConnection();
			String sql = "select * from topology where node_id = ?";
			getRoot = conn.prepareStatement(sql);
			getRoot.setInt(1, rootID);
			rootRS = getRoot.executeQuery();
			
			if (rootRS.next()) {
				int maxDepth = rootRS.getInt("Depth") + depth;
				
				sql = "select * " + 
					" from node natural join topology " + 
					" where LeftNode >= ? and RightNode <= ? and Depth <= ? and tree_id = ?" + 
					" order by Depth desc ";
				
				getSubtree = conn.prepareStatement(sql);
				getSubtree.setInt(1, rootRS.getInt("LeftNode"));
				getSubtree.setInt(2, rootRS.getInt("RightNode"));
				getSubtree.setInt(3, maxDepth);
				getSubtree.setInt(4, rootRS.getInt("tree_id"));
				
				subtreeRS = getSubtree.executeQuery();
				
				subtree = buildTree(subtreeRS);
			}
		}
		catch(SQLException e)
		{
			//Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			ConnectionAdapter.close(rootRS);
			ConnectionAdapter.close(subtreeRS);
			ConnectionAdapter.close(getRoot);
			ConnectionAdapter.close(getSubtree);
			ConnectionAdapter.close(conn);
		}
		
		return subtree;
	}

	@Override
	public RemoteNode[] getChildren(int parentID)
	{
		RemoteNode[] children = null;
		Connection conn = null;
		
		try
		{
			conn = pool.getConnection();
			int depth = 0;
			children = getChildren(parentID, depth, conn);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			ConnectionAdapter.close(conn);
		}
		
		return children;
	}
	
	public RemoteNode[] getChildren(int parentID, int depth, Connection conn) throws SQLException
	{
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>();
		String sql = "select * from node natural join topology where parent_id = ?";
		PreparedStatement getChildrenStmt = conn.prepareStatement(sql);
		getChildrenStmt.setInt(1, parentID);
		
		ResultSet rs = getChildrenStmt.executeQuery();
		
		while (rs.next()) {
			RemoteNode child = createNode(rs);
			
			if (depth > 0 && child.getNumberOfChildren() > 0) 
			{
				child.setChildren(getChildren(child.getId(), depth - 1, conn));
			}
			
			children.add(child);
		}
		
		ConnectionAdapter.close(rs);
		
		if (children.size() > 0) {
			return children.toArray(new RemoteNode[children.size()]);
		} else {
			return null;
		}
	}

	@Override
	public Tree getTree(int id, int depth)
	{
		Tree tree = null;
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
	
		try
		{
			conn = pool.getConnection();
			String sql = "select * from tree where tree_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1,id);
			
			rs = statement.executeQuery();
			if (rs.next()) {
				tree = new Tree();
				tree.setId(rs.getInt("tree_id"));
				int rootId = rs.getInt("root_id");
				RemoteNode node = getSubtree(rootId, depth);
				tree.setRootNode(node);
			}
		}
		catch(SQLException e)
		{
			//Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			ConnectionAdapter.close(rs);
			ConnectionAdapter.close(statement);
			ConnectionAdapter.close(statement);
			ConnectionAdapter.close(conn);
		}
		
		return tree;
	}


	/**
	 * Takes the ResultSet from getSubtree and builds a subtree from it. 
	 * 
	 * The nodes in subtreeRS must be sorted by Depth from largest to smallest.
	 */
	private RemoteNode buildTree(ResultSet subtreeRS) throws SQLException
	{
		HashMap<Integer,List<RemoteNode>> childrenLists = new HashMap<Integer,List<RemoteNode>>();
		RemoteNode node = null;

		while(subtreeRS.next())
		{
			//create the node
			node = createNode(subtreeRS);
			
			List<RemoteNode> childrenList = childrenLists.get(node.getId());
			if(childrenList != null)
			{
				RemoteNode[] children = childrenList.toArray(new RemoteNode[childrenList.size()]);
				node.setChildren(children);
			}
			
			//add the node to its parent's childrenList (creating the list first if it doesn't already exist)
			int parentID = subtreeRS.getInt("parent_id");
			if(!childrenLists.containsKey(parentID))
			{
				childrenLists.put(parentID, new ArrayList<RemoteNode>());
			}
			
			childrenLists.get(parentID).add(node);
		}
		
		return node; //the last row of the resultset was the root node 
	}
	
	/**
	 * Creates a RemoteNode from the current row of a ResultSet.
	 * 
	 * @param rs A ResultSet containing the columns of the node and topology tables. The state of the
	 *            ResultSet (current row, etc) should not be altered by this method
	 * @throws SQLException 
	 */
	public static RemoteNode createNode(ResultSet rs) throws SQLException
	{
		int id = rs.getInt("node_id");
		String label = rs.getString("Label");
		int numNodes = rs.getInt("NumNodes");
		int numLeaves = rs.getInt("NumLeaves");
		int height = rs.getInt("Height");
		int depth = rs.getInt("Depth");
		int numChildren = rs.getInt("NumChildren");
		int leftIndex = rs.getInt("LeftNode");
		int rightIndex = rs.getInt("RightNode");

		return new RemoteNode(id, label, numChildren, numNodes, numLeaves, depth, height, leftIndex, rightIndex);
	}
	

	@Override
	public String getTrees() {
		
		JSONObject result = new JSONObject();
		JSONArray trees = new JSONArray();
		
		Connection conn;
		try {
			conn = pool.getConnection();
		
			PreparedStatement statement = conn.prepareStatement("select * from tree");
			
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				
				int uuid = rs.getInt("tree_id");
				String name = rs.getString("Name");
				
				trees.put(buildJSONForTree(uuid,name));
	
			}
			
			result.put("trees", trees);
			
			ConnectionAdapter.close(statement);
			ConnectionAdapter.close(conn);
			ConnectionAdapter.close(rs);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return result.toString();
	}
	
	private JSONObject buildJSONForTree(int id, String name) throws JSONException {
		JSONObject tree = new JSONObject();
		tree.put("id", id);
		tree.put("name", name);
		return tree;
	}
}
