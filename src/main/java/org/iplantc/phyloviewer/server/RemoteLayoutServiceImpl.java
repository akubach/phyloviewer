package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RemoteLayoutServiceImpl extends RemoteServiceServlet implements RemoteLayoutService {
	private static final long serialVersionUID = 7624599785344982721L;

	public void init() {
		System.out.println("Starting RemoteLayoutServiceImpl");
		this.getServletContext().setAttribute("org.iplantc.phyloviewer.server.RemoteLayoutServiceImpl", this);
	}
	
	private ILayoutData getLayoutData() {
		return (ILayoutData) this.getServletContext().getAttribute(Constants.LAYOUT_DATA_KEY);
	}

	@Override
	public String layout(String treeID, ILayout layout) {
		ILayoutData layoutData = this.getLayoutData();
		return layoutData.getLayout(layout.getClass(), treeID);
	}
	
	@Override
	public LayoutResponse getLayout(INode node, String layoutID) throws Exception {		
		return this.getLayoutData().getLayout(node,layoutID);
	}
	
	@Override
	public LayoutResponse[] getLayout(INode[] nodes, String layoutID) throws Exception {
		LayoutResponse[] response = new LayoutResponse[nodes.length];
		
		for (int i = 0; i < nodes.length; i++) {
			response[i] = getLayout(nodes[i], layoutID);
		}
		
		return response;
	}
}
