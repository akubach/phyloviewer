package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.services.CombinedService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CombinedServiceImpl extends RemoteServiceServlet implements CombinedService
{
	private static final long serialVersionUID = 2839219371009200675L;
	
	private ITreeData getTreeData() {
		return (ITreeData) this.getServletContext().getAttribute(Constants.TREE_DATA_KEY);
	}
	
	private ILayoutData getLayoutData() {
		return (ILayoutData) this.getServletContext().getAttribute(Constants.LAYOUT_DATA_KEY);
	}

	@Override
	public RemoteNode[] getChildren(int parentID) {
		return this.getTreeData().getChildren(parentID);
	}

	@Override
	public Tree getTree(int id) 
	{
		ITreeData treeData = this.getTreeData();
		Tree tree = treeData.getTree(id, 0);
		return tree;
	}
	
	public Tree getTree(int id, int depth) {
		return this.getTreeData().getTree(id, depth);
	}

	@Override
	public LayoutResponse getLayout(INode node) throws Exception {		
		return this.getLayoutData().getLayout(node);
	}
	
	@Override
	public LayoutResponse[] getLayout(INode[] nodes) throws Exception {
		LayoutResponse[] response = new LayoutResponse[nodes.length];
		
		for (int i = 0; i < nodes.length; i++) {
			response[i] = getLayout(nodes[i]);
		}
		
		return response;
	}

	@Override
	public CombinedResponse getChildrenAndLayout(int parentID) throws Exception
	{
		CombinedResponse response = new CombinedResponse();
	
		response.parentID = parentID;
		response.nodes = getChildren(parentID);
		response.layouts = getLayout(response.nodes);
		
		return response;
	}

	@Override
	public CombinedResponse[] getChildrenAndLayout(int[] parentIDs) throws Exception
	{
//		Util.simulateDelay(this.getThreadLocalRequest(), 100);
		System.out.println("Getting children and layouts for " + parentIDs.length + " parents");
		
		CombinedResponse[] responses = new CombinedResponse[parentIDs.length];
		for (int i = 0; i < parentIDs.length; i++) {
			responses[i] = getChildrenAndLayout(parentIDs[i]);
		}
		return responses;
	}

}
