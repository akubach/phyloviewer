package org.iplantc.phyloviewer.server;

import java.util.concurrent.ConcurrentHashMap;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.UUID;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RemoteLayoutServiceImpl extends RemoteServiceServlet implements RemoteLayoutService {
	private static final long serialVersionUID = 7624599785344982721L;
	private static ConcurrentHashMap<String, ILayout> layouts = new ConcurrentHashMap<String, ILayout>();
	private RemoteNodeService nodeService = new RemoteNodeServiceImpl();

	public void setNodeService(RemoteNodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	@Override
	public String layout(int i, ILayout layout) {
		return this.layout(nodeService.fetchTree(i), layout);
	}
	
	@Override
	public String layout(String treeID, ILayout layout) {
		return this.layout(nodeService.fetchTree(treeID), layout);
	}
	
	private String layout(Tree tree, ILayout layout) {
		layout.layout(tree);
		String uuid = UUID.uuid();
		putLayout(uuid, layout);
		return uuid;
	}

	private void putLayout(String layoutID, ILayout layout) {
		layouts.put(layoutID, layout);
	}
	
	private ILayout getLayout(String layoutID) {
		return layouts.get(layoutID);
	}

	@Override
	public LayoutResponse getLayout(INode node, String layoutID) {
		LayoutResponse response = new LayoutResponse();
		response.boundingBox = this.getLayout(layoutID).getBoundingBox(node);
		response.position = this.getLayout(layoutID).getPosition(node);
		
		return response;
	}

}
