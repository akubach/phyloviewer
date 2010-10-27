package org.iplantc.phyloviewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.services.SearchService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.server.db.ConnectionAdapter;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SearchServiceImpl extends RemoteServiceServlet implements SearchService
{
	private static final long serialVersionUID = -7938571144166651105L;

	@Override
	public RemoteNode[] find(String query, int tree, Type type)
	{
		//TODO sanitize the query string?  or does the PreparedStatement make that unnecessary?
		//TODO escape any SQL wildcards
		String queryString = type.queryString(query);
		
		ArrayList<RemoteNode> results = new ArrayList<RemoteNode>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = getConnection();
			stmt = conn.prepareStatement("select * from node natural join topology where tree_id = ? and lower(label) like lower(?)"); //TODO if this is slow, make an index on lower(label) or use ILIKE (which is not standard SQL)
			stmt.setInt(1, tree);
			stmt.setString(2, queryString);
			rs = stmt.executeQuery();
			
			while (rs.next())
			{
				RemoteNode node = DatabaseTreeData.createNode(rs);
				results.add(node);
			}
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			ConnectionAdapter.close(conn);
			ConnectionAdapter.close(stmt);
			ConnectionAdapter.close(rs);
		}
		
		
		return results.toArray(new RemoteNode[results.size()]);
	}

	private Connection getConnection() {
		Connection conn = null;
		
		try
		{
			DataSource pool = (DataSource) getServletContext().getAttribute("db.connectionPool");
			conn = pool.getConnection();
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
	}
}
