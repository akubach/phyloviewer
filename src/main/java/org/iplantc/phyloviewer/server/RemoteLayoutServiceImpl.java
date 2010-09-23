package org.iplantc.phyloviewer.server;

import java.util.concurrent.ConcurrentHashMap;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayoutCircular;
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
	
	//making an index of layout ids on the composite key (layout class, tree id).  This is an ugly thing that will go away when we start putting the layouts in a database.
	private static ConcurrentHashMap<Class<? extends ILayout>, ConcurrentHashMap<String, String>> treeLayoutIndex = new ConcurrentHashMap<Class<? extends ILayout>, ConcurrentHashMap<String, String>>();

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
		String uuid;
		
		if (containsLayout(layout.getClass(), tree.getId())) {
			uuid = getLayout(layout.getClass(), tree.getId());
		} else {
			layout.layout(tree);
			uuid = UUID.uuid();
			putLayout(uuid, layout, tree);
		}
		
		return uuid;
	}

	private void putLayout(String layoutID, ILayout layout, Tree tree) {
		layouts.put(layoutID, layout);
		
		if (treeLayoutIndex.containsKey(layout.getClass())) {
			treeLayoutIndex.get(layout.getClass()).put(tree.getId(), layoutID);
		} else {
			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
			map.put(tree.getId(), layoutID);
			treeLayoutIndex.put(layout.getClass(), map);
		}
	}
	
	private ILayout getLayout(String layoutID) {
		return layouts.get(layoutID);
	}
	
	private String getLayout(Class<? extends ILayout> layoutClass, String treeId) {
		String layoutId = null;
		
		if (treeLayoutIndex.containsKey(layoutClass)) {
			layoutId = treeLayoutIndex.get(layoutClass).get(treeId);
		}
		
		return layoutId;
	}
	
	private boolean containsLayout(Class<? extends ILayout> layoutClass, String treeId) {
		return treeLayoutIndex.containsKey(layoutClass) && treeLayoutIndex.get(layoutClass).containsKey(treeId);
	}

	@Override
	public LayoutResponse getLayout(INode node, String layoutID) throws Exception {
		LayoutResponse response = new LayoutResponse();
		response.layoutID = layoutID;
		response.nodeID = node.getUUID();
		ILayout layout = this.getLayout(layoutID);

		if (layout == null) {
			throw new Exception("layout " + layoutID + " not found.");
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
	
	@Override
	public LayoutResponse[] getLayout(INode[] nodes, String layoutID) throws Exception {
		LayoutResponse[] response = new LayoutResponse[nodes.length];
		
		for (int i = 0; i < nodes.length; i++) {
			response[i] = getLayout(nodes[i], layoutID);
		}
		
		return response;
	}
}
