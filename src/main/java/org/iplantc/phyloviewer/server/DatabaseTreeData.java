package org.iplantc.phyloviewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public class DatabaseTreeData implements TreeData
{
	private DataSource pool;

	public DatabaseTreeData(ServletContext servletContext)
	{
		this((DataSource)servletContext.getAttribute("db.connectionPool"));
	}
	
	public DatabaseTreeData(DataSource pool) {
		this.pool = pool;
		initTables();
	}
	
	@Override
	public RemoteNode getSubtree(String rootID, int depth)
	{
		RemoteNode subtree = null;
		Connection conn = null;
		PreparedStatement getRoot = null;
		PreparedStatement getSubtree = null;
		ResultSet rootRS = null;
		ResultSet subtreeRS = null;
		
		try
		{
			//TODO combining the two selects or creating a stored procedure to do both selects on the db might speed this up
			
			//using some extra topology metadata to get the entire subtree in two queries instead of recursively querying for children
			conn = pool.getConnection();
			String sql = "select Left, Right, Depth from Topology where ID = ?";
			getRoot = conn.prepareStatement(sql);
			getRoot.setString(1, rootID);
			rootRS = getRoot.executeQuery();
			
			if (rootRS.next()) {
				int maxDepth = rootRS.getInt("Depth") + depth;
				
				sql = "select Node.ID, Label, ParentID, Height, NumLeaves, NumNodes, NumChildren " + 
					" from Node natural join Topology " + 
					" where Left >= ? and Right <= ? and Depth <= ? " + 
					" order by Depth desc ";
				
				getSubtree = conn.prepareStatement(sql);
				getSubtree.setInt(1, rootRS.getInt("Left"));
				getSubtree.setInt(2, rootRS.getInt("Right"));
				getSubtree.setInt(3, maxDepth);
				
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
			close(rootRS);
			close(subtreeRS);
			close(getRoot);
			close(getSubtree);
			close(conn);
		}
		
		return subtree;
	}

	@Override
	public RemoteNode[] getChildren(String parentID)
	{
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>();
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try
		{
			conn = pool.getConnection();
			String sql = "select * from Node natural join Topology where ParentID = ?";
			statement = conn.prepareStatement(sql);
			
			statement.setString(1, parentID);
			
			rs = statement.executeQuery();
			
			while (rs.next()) {
				
				String uuid = rs.getString("ID");
				String label = rs.getString("Label");
				int numNodes = rs.getInt("NumNodes");
				int numLeaves = rs.getInt("NumLeaves");
				int height = rs.getInt("Height");
				int numChildren = rs.getInt("NumChildren");
				RemoteNode child = new RemoteNode(uuid, label, numNodes, numLeaves, height, numChildren);
				children.add(child);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			close(rs);
			close(statement);
			close(conn);
		}
		
		if (children.size() > 0) {
			return children.toArray(new RemoteNode[children.size()]);
		} else {
			return null;
		}
	}

	@Override
	public Tree getTree(String id, int depth)
	{
		Tree tree = null;
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
	
		try
		{
			conn = pool.getConnection();
			String sql = "select * from Tree where ID = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, id);
			
			rs = statement.executeQuery();
			if (rs.next()) {
				tree = new Tree();
				tree.setId(rs.getString("ID"));
				String rootId = rs.getString("RootID");
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
			close(rs);
			close(statement);
			close(conn);
		}
		
		return tree;
	}

	@Override
	public void addTree(Tree tree)
	{
		Connection conn = null;
		PreparedStatement statement = null;
		
		try
		{
			//adds the tree in a single transaction
			conn = pool.getConnection();
			conn.setAutoCommit(false);
			
			RemoteNode root = (RemoteNode) tree.getRootNode();
			addSubtree(root, null, 0, 0, conn); //note: tree root will have null parentID in the DB
			//TODO passing one PreparedStatement through the whole tree and using batching might speed this up
			
			String sql = "merge into Tree(ID, RootID) values(?, ?)";
			statement = conn.prepareStatement(sql);
			statement.setString(1, tree.getId());
			statement.setString(2, tree.getRootNode().getUUID());
			statement.executeUpdate();
			
			conn.commit();
		}
		catch(SQLException e)
		{
			//rolls back entire tree transaction on exception anywhere in the tree
			rollback(conn);
			e.printStackTrace();
		}
		finally
		{
			close(statement);
			close(conn);
		}
	}
	
	private int addSubtree(RemoteNode node, String parentID, int traversalCount, int depth, Connection conn) throws SQLException 
	{
		addRemoteNode(node, conn);
		
		int left = traversalCount;
		traversalCount++;
		
		for (RemoteNode child : node.getChildren())
		{
			traversalCount = addSubtree(child, node.getUUID(), traversalCount, depth + 1, conn);
		}
		
		int right = traversalCount;
		traversalCount++;
		addChild(parentID, node, left, right, depth, conn);
		
		return traversalCount;
	}

	private void addRemoteNode(RemoteNode node, Connection conn) throws SQLException
	{
		PreparedStatement statement = null;
		try 
		{
			String sql = "merge into Node(ID, Label) values (?, ?)";
			statement = conn.prepareStatement(sql);
			
			statement.setString(1, node.getUUID());
			statement.setString(2, node.getLabel());

			
			statement.executeUpdate();
		}
		finally
		{
			close(statement);
		}
	}
	
	private void addChild(String parentID, RemoteNode child, int left, int right, int depth, Connection conn) throws SQLException
	{
		PreparedStatement statement = null;
		try {
			String sql = "merge into Topology (ID, ParentID, NumNodes, NumLeaves, Height, Left, Right, Depth, NumChildren) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			statement = conn.prepareStatement(sql);
			
			statement.setString(1, child.getUUID());
			statement.setString(2, parentID);
			statement.setInt(3, child.getNumberOfNodes());
			statement.setInt(4, child.getNumberOfLeafNodes());
			statement.setInt(5, child.findMaximumDepthToLeaf());
			statement.setInt(6, left);
			statement.setInt(7, right);
			statement.setInt(8, depth);
			statement.setInt(9, child.getNumberOfChildren());
		
			statement.executeUpdate();
		}
		finally
		{
			close(statement);
		}
	}
	
	/**
	 * Takes the ResultSet from getSubtree and builds a subtree from it. 
	 * 
	 * The nodes in subtreeRS must be sorted by Depth from largest to smallest.
	 */
	private RemoteNode buildTree(ResultSet subtreeRS) throws SQLException
	{
		HashMap<String,List<RemoteNode>> childrenLists = new HashMap<String,List<RemoteNode>>();
		RemoteNode root = null;

		while(subtreeRS.next())
		{
			String parentID = subtreeRS.getString("ParentID");

			if(!childrenLists.containsKey(parentID))
			{
				childrenLists.put(parentID, new ArrayList<RemoteNode>());
			}

			String uuid = subtreeRS.getString("ID");
			String label = subtreeRS.getString("Label");
			int numNodes = subtreeRS.getInt("NumNodes");
			int numLeaves = subtreeRS.getInt("NumLeaves");
			int height = subtreeRS.getInt("Height");
			int numChildren = subtreeRS.getInt("NumChildren");

			List<RemoteNode> childrenList = childrenLists.get(uuid);
			
			RemoteNode node;
			if(childrenList != null) {
				RemoteNode[] children = childrenList.toArray(new RemoteNode[childrenList.size()]);
				node = new RemoteNode(uuid, label, numNodes, numLeaves, height, children);
			} else {
				node = new RemoteNode(uuid, label, numNodes, numLeaves, height, numChildren); //TODO get the number of children
			}
			
			childrenLists.get(parentID).add(node);
			
			root = node;
		}
		
		return root;
	}

	private void initTables()
	{
		Connection conn = null;
		Statement statement = null;
		try
		{
			conn = pool.getConnection();
			statement = conn.createStatement();
			
			statement.execute("create table if not exists Node (ID uuid primary key, Label varchar)");
			statement.execute("create table if not exists Tree (ID uuid primary key, RootID uuid not null, foreign key(RootID) references Node(ID))");
			statement.execute("create table if not exists Topology (ID uuid primary key, ParentID uuid, Left int, Right int, Depth int, Height int, NumChildren int, NumLeaves int, NumNodes int, foreign key(ID) references Node(ID), foreign key(ParentID) references Node(ID))");
			
			statement.execute("create index if not exists IndexParent on Topology(ParentID)");
			statement.execute("create index if not exists IndexLeft on Topology(Left)");
			statement.execute("create index if not exists IndexRight on Topology(Right)");
			statement.execute("create index if not exists IndexDepth on Topology(Depth)");

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(statement);
			close(conn);
		}
	}
	
	private void dropTables() {
		Connection conn = null;
		Statement statement = null;
		try
		{
			conn = pool.getConnection();
			statement = conn.createStatement();
			statement.execute("drop table Node");
			statement.execute("drop table Tree");
			statement.execute("drop table Topology");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(statement);
			close(conn);
		}
	}
	
	private void rollback(Connection conn) {
		try
		{
			conn.rollback();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void close(Connection conn) {
		try
		{
			if (conn != null) 
			{
				conn.close();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void close(Statement statement) {
		try
		{
			if (statement != null)
			{
				statement.close();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void close(ResultSet resultSet) {
		try
		{
			if (resultSet != null) 
			{
				resultSet.close();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
