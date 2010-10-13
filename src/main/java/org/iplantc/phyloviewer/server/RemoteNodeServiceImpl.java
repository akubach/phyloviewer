package org.iplantc.phyloviewer.server;

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

	@Override
	public RemoteNode[] getChildren(String parentID) {
		return this.getTreeData().getChildren(parentID);
	}

	@Override
	public Tree getTree(String id) 
	{
		ITreeData treeData = this.getTreeData();
		Tree tree = treeData.getTree(id, 0);
		return tree;
	}
	
	public Tree getTree(String id, int depth) {
		return this.getTreeData().getTree(id, depth);
	}
}
