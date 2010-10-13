package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.DemoTree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RemoteNodeServiceImpl extends RemoteServiceServlet implements RemoteNodeService {
	private static final long serialVersionUID = 3050278763811296728L;

	public void init() {
		System.out.println("Starting RemoteNodeServiceImpl");
		this.getServletContext().setAttribute("org.iplantc.phyloviewer.server.RemoteNodeServiceImpl", this);
	}
	
	private ITreeData getTreeData() {
		return (ITreeData) this.getServletContext().getAttribute(Constants.TREE_DATA_KEY);
	}
	
	private ILayoutData getLayoutData() {
		return (ILayoutData) this.getServletContext().getAttribute(Constants.LAYOUT_DATA_KEY);
	}
	
	private IOverviewImageData getOverviewData() {
		return (IOverviewImageData) this.getServletContext().getAttribute(Constants.OVERVIEW_DATA_KEY);
	}

	@Override
	public RemoteNode[] getChildren(String parentID) {
		return this.getTreeData().getChildren(parentID);
	}

	@Override
	public Tree getTree(String id) 
	{
		Tree tree;
		
		synchronized (id) //TODO id is probably not a good lock
		{ 
			ITreeData treeData = this.getTreeData();
			tree = treeData.getTree(id, 0);
		
			//this may be the id for a demo tree thats not loaded yet
			if (tree == null) 
			{
				DemoTree demoTree = DemoTree.byID(id);
				
				if (demoTree != null) 
				{
					String json = FetchTreeImpl.fetchTree(demoTree,getServletContext());
					LoadTreeData.loadTreeDataFromJSON(demoTree.id, json, this.getTreeData(), this.getLayoutData(), this.getOverviewData());
					
					tree = treeData.getTree(id, 0);
				}
			}
		}
		
		return tree;
	}
	
	public Tree getTree(String id, int depth) {
		return this.getTreeData().getTree(id, depth);
	}
}
