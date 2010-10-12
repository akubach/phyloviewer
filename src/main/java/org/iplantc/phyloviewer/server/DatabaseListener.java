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
		
		DatabaseTreeData treeData = new DatabaseTreeData(servletContext);
		servletContext.setAttribute(Constants.TREE_DATA_KEY, treeData);
		
		DatabaseLayoutData layoutData = new DatabaseLayoutData(servletContext);
		servletContext.setAttribute(Constants.LAYOUT_DATA_KEY, layoutData);
		
		DatabaseOverviewImage overivewData = new DatabaseOverviewImage();
		servletContext.setAttribute(Constants.OVERVIEW_DATA_KEY, overivewData);
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
