package org.iplantc.phyloviewer.server;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayoutCircular;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class DatabaseLayoutData implements ILayoutData {

	@SuppressWarnings("unused")
	private DataSource pool;
	
	private static ConcurrentHashMap<String, ILayout> layouts;
	
	//making an index of layout ids on the composite key (layout class, tree id).  This is an ugly thing that will go away when we start putting the layouts in a database.
	private static ConcurrentHashMap<Class<? extends ILayout>, ConcurrentHashMap<String, String>> treeLayoutIndex = new ConcurrentHashMap<Class<? extends ILayout>, ConcurrentHashMap<String, String>>();

	public DatabaseLayoutData(ServletContext servletContext)
	{
		this((DataSource)servletContext.getAttribute("db.connectionPool"));
	}
	
	public DatabaseLayoutData(DataSource pool) {
		this.pool = pool;
		initTables();
		
		layouts = new ConcurrentHashMap<String, ILayout>();
	}
	
	private void initTables() {
		// TODO Auto-generated method stub
		
	}

	public void putLayout(String layoutID, ILayout layout, ITree tree) {
		layouts.put(layoutID, layout);
		
		if (treeLayoutIndex.containsKey(layout.getClass())) {
			treeLayoutIndex.get(layout.getClass()).put(tree.getId(), layoutID);
		} else {
			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
			map.put(tree.getId(), layoutID);
			treeLayoutIndex.put(layout.getClass(), map);
		}
	}
	
	public ILayout getLayout(String layoutID) {
		return layouts.get(layoutID);
	}
	
	public boolean containsLayout(Class<? extends ILayout> layoutClass, String treeId) {
		return treeLayoutIndex.containsKey(layoutClass) && treeLayoutIndex.get(layoutClass).containsKey(treeId);
	}
	
	public String getLayout(Class<? extends ILayout> layoutClass, String treeId) {
		String layoutId = null;
		
		if (treeLayoutIndex.containsKey(layoutClass)) {
			layoutId = treeLayoutIndex.get(layoutClass).get(treeId);
		}
		
		return layoutId;
	}

	public LayoutResponse getLayout(INode node, String layoutID) throws Exception {
		LayoutResponse response = new LayoutResponse();
		response.layoutID = layoutID;
		response.nodeID = node.getUUID();
		ILayout layout = this.getLayout(layoutID);

		if (layout == null) {
			throw new Exception("layout " + layoutID + " not found.");
		}
		else if (!layout.containsNode(node)) 
		{
			throw new Exception("layout " + layoutID + " does not contain node " + node.getUUID());
		}
		
		response.boundingBox = layout.getBoundingBox(node);
		response.position = layout.getPosition(node);
		
		if (layout instanceof ILayoutCircular) { 
			ILayoutCircular lc = (ILayoutCircular)layout;
			response.polarBounds = lc.getPolarBoundingBox(node);
			response.polarPosition = lc.getPolarPosition(node);
		}
		
		return response;
	}
}
