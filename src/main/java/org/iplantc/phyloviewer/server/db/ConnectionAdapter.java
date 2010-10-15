package org.iplantc.phyloviewer.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionAdapter {

	protected Connection connection;
	
	protected ConnectionAdapter(Connection conn) {
		this.connection = conn;
	}
	
	protected static void close(Connection conn) {
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
	
	protected static void close(Statement statement) {
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
}
