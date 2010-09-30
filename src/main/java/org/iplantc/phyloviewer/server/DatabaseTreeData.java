package org.iplantc.phyloviewer.server;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public class DatabaseTreeData implements TreeData
{
	DataSource pool;

	public DatabaseTreeData(ServletContext servletContext)
	{
		pool = (DataSource)servletContext.getAttribute("db.connectionPool");
		initTables();
	}

	@Override
	public RemoteNode[] getChildren(String parentID)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tree getTree(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTree(Tree tree)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void addRemoteNode(RemoteNode node)
	{
		// TODO Auto-generated method stub
	}

	private Connection getConnection()
	{
		Connection conn = null;
		try
		{
			conn = pool.getConnection();
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return conn;
	}

	private void initTables()
	{
		Connection conn = getConnection();
		try
		{
			conn.createStatement().execute(
					"create table if not exists TREE (ID uuid primary key, ROOTID uuid not null)");
			conn
					.createStatement()
					.execute(
							"create table if not exists NODE (ID uuid primary key, LABEL varchar, NUMLEAVES int, HEIGHT int)");
			conn
					.createStatement()
					.execute(
							"create table if not exists PARENT (CHILDID uuid primary key, PARENTID uuid not null)");
			conn.createStatement()
					.execute("create index if not exists INDEX_PARENT on PARENT(PARENTID)");
			conn.commit();
			conn.close();
		}
		catch(SQLException e)
		{
			// TODO: handle exception
		}
	}
}
