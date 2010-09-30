package org.iplantc.phyloviewer.server;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.ConnectionPoolDataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;

public class DatabaseListener implements ServletContextListener
{
	JdbcConnectionPool pool;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent)
	{

		ServletContext servletContext = contextEvent.getServletContext();
		ConnectionPoolDataSource ds = getDataSource(servletContext);
		pool = JdbcConnectionPool.create(ds);
		servletContext.setAttribute("db.connectionPool", pool);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		try
		{
			pool.dispose();
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ConnectionPoolDataSource getDataSource(ServletContext servletContext)
	{
		JdbcDataSource jdbcDataSource = new JdbcDataSource();

		String url = servletContext.getInitParameter("db.url");
		String user = servletContext.getInitParameter("db.user");
		String password = servletContext.getInitParameter("db.password");
		jdbcDataSource.setURL(url);
		jdbcDataSource.setUser(user);
		jdbcDataSource.setPassword(password);

		return jdbcDataSource;
	}
}
