package org.iplantc.phyloviewer.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionAdapter {

	protected Connection connection;
	
	protected ConnectionAdapter(Connection conn) {
		this.connection = conn;
	}
	
	public static void close(Connection conn) {
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
	
	public static void close(Statement statement) {
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
	

	
	public static void close(ResultSet resultSet) {
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
