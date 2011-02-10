package org.iplantc.phyloviewer.viewer.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtil {

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
	
	public static void rollback(Connection conn) {
		try
		{
			conn.rollback();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
