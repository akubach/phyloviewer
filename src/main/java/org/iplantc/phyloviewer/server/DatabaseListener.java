package org.iplantc.phyloviewer.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.iplantc.phyloviewer.server.db.ImportTreeData;
import org.postgresql.ds.PGPoolingDataSource;

public class DatabaseListener implements ServletContextListener
{
	PGPoolingDataSource pool;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent)
	{
		ServletContext servletContext = contextEvent.getServletContext();
		
		String server = servletContext.getInitParameter("db.server");
		String database = servletContext.getInitParameter("db.database");
		String user = servletContext.getInitParameter("db.user");
		String password = servletContext.getInitParameter("db.password");
		
		pool = new PGPoolingDataSource();
		pool.setServerName(server);
		pool.setDatabaseName(database);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setMaxConnections(10);
		
		servletContext.setAttribute("db.connectionPool", pool);
		
		DatabaseTreeData treeData = new DatabaseTreeData(pool);
		servletContext.setAttribute(Constants.TREE_DATA_KEY, treeData);
		
		DatabaseLayoutData layoutData = new DatabaseLayoutData(pool);
		servletContext.setAttribute(Constants.LAYOUT_DATA_KEY, layoutData);
		
		DatabaseOverviewImage overviewData = new DatabaseOverviewImage(pool);
		servletContext.setAttribute(Constants.OVERVIEW_DATA_KEY, overviewData);
		
		ImportTreeData importer = new ImportTreeData(pool,servletContext.getRealPath("/images/"));
		servletContext.setAttribute(Constants.IMPORT_TREE_DATA_KEY, importer);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		pool.close();
	}
}
