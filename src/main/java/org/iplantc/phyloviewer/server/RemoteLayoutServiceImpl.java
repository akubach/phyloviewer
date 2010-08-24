package org.iplantc.phyloviewer.server;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.UUID;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RemoteLayoutServiceImpl extends RemoteServiceServlet implements RemoteLayoutService {
	private static final long serialVersionUID = 7624599785344982721L;
	private RemoteNodeService nodeService = new RemoteNodeServiceImpl();
	private Map<String, ILayout> layouts = new HashMap<String, ILayout>();

	public void setNodeService(RemoteNodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	@Override
	public String layout(int i, ILayout layout) {
		Tree tree = nodeService.fetchTree(i); //note tree is local, not serialized, so I should have access to the whole tree
		layout.layout(tree);
		String uuid = UUID.uuid();
		putLayout(uuid, layout);
		return uuid;
	}

	private void putLayout(String layoutID, ILayout layout) {
		this.layouts.put(layoutID, layout);
	}
	
	private ILayout getLayout(String layoutID) {
		return this.layouts.get(layoutID);
	}

	@Override
	public LayoutResponse getLayout(INode node, String layoutID) {
		LayoutResponse response = new LayoutResponse();
		response.boundingBox = this.getLayout(layoutID).getBoundingBox(node);
		response.position = this.getLayout(layoutID).getPosition(node);
		
		return response;
	}

}
