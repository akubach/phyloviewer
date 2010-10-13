package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.services.CombinedService;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CombinedServiceImpl extends RemoteServiceServlet implements CombinedService
{
	private static final long serialVersionUID = 2839219371009200675L;
	
	private RemoteNodeService getNodeService() {
		return (RemoteNodeService) this.getServletContext().getAttribute("org.iplantc.phyloviewer.server.RemoteNodeServiceImpl");
	}
	
	private RemoteLayoutService getLayoutService() {
		return (RemoteLayoutService) this.getServletContext().getAttribute("org.iplantc.phyloviewer.server.RemoteLayoutServiceImpl");
	}

	@Override
	public RemoteNode[] getChildren(String parentID)
	{
		return getNodeService().getChildren(parentID);
	}

	@Override
	public Tree getTree(String id)
	{
		return getNodeService().getTree(id);
	}

	@Override
	public LayoutResponse getLayout(INode node, String layoutID) throws Exception
	{
		return getLayoutService().getLayout(node, layoutID);
	}

	@Override
	public LayoutResponse[] getLayout(INode[] nodes, String layoutID) throws Exception
	{
		return getLayoutService().getLayout(nodes, layoutID);
	}

	@Override
	public String layout(String treeID, ILayout layout)
	{
		return getLayoutService().layout(treeID, layout);
	}

	@Override
	public CombinedResponse getChildrenAndLayout(String parentID, String layoutID) throws Exception
	{
		CombinedResponse response = new CombinedResponse();
	
		response.parentID = parentID;
		response.layoutID = layoutID;
		response.nodes = getChildren(parentID);
		response.layouts = getLayout(response.nodes, layoutID);
		
		return response;
	}

	@Override
	public CombinedResponse[] getChildrenAndLayout(String[] parentIDs, String[] layoutIDs) throws Exception
	{
		CombinedResponse[] responses = new CombinedResponse[parentIDs.length];
		for (int i = 0; i < parentIDs.length; i++) {
			responses[i] = getChildrenAndLayout(parentIDs[i], layoutIDs[i]);
		}
		return responses;
	}

}
