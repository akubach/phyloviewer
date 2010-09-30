package org.iplantc.phyloviewer.server;

import java.util.concurrent.ConcurrentHashMap;

import org.iplantc.phyloviewer.client.FetchTree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RemoteNodeServiceImpl extends RemoteServiceServlet implements RemoteNodeService {
	private static final long serialVersionUID = 3050278763811296728L;
	private static final ConcurrentHashMap<String, RemoteNode> nodes = new ConcurrentHashMap<String, RemoteNode>();
	private static final ConcurrentHashMap<String, Tree> trees = new ConcurrentHashMap<String, Tree>();

	public void init() {
		this.getServletContext().setAttribute("org.iplantc.phyloviewer.server.RemoteNodeServiceImpl", this);
		//TODO go ahead and pre-fetch the demo trees here?
	}
	
	public FetchTree getFetchTree() {
		return (FetchTree) this.getServletContext().getAttribute("org.iplantc.phyloviewer.server.FetchTreeImpl");
	}
	
	public void setFetchTree(FetchTree fetchTree) {
		this.getServletContext().setAttribute("org.iplantc.phyloviewer.server.FetchTreeImpl", fetchTree);
	}
	
	@Override
	public RemoteNode[] getChildren(String parentID) {
		//note: the children will be serialized *without* their children (which is a transient field)
		return nodes.get(parentID).getChildren();
	}
	
	@Override
	public Tree getTree(int i) {
		Tree tree;
		
		if (trees.containsKey(Integer.toString(i))) {
			tree = trees.get(Integer.toString(i));
		} else {
			String json = getFetchTree().fetchTree(i);

			JSONObject root = parseTree(json);

			RemoteNode remoteRoot = mapSubtree(root);
			tree = new Tree();
			tree.setId(Integer.toString(i));
			tree.setRootNode(remoteRoot);
			addTree(tree);
		}
		
		return tree;
	}

	@Override
	public Tree getTree(String id) {
		return trees.get(id);
	}
	
	private void addTree(Tree tree) {
		trees.put(tree.getId(), tree);
		storeSubtree((RemoteNode) tree.getRootNode());
	}
	
	private void storeSubtree(RemoteNode root) {
		RemoteNodeServiceImpl.this.addRemoteNode(root);
		
		for (RemoteNode child : root.getChildren()) {
			storeSubtree(child);
		}
	}
	
	protected void addRemoteNode(RemoteNode node) {
		nodes.put(node.getUUID(), node);
	}
	
	/**
	 * Translates a JSONObject to a tree of RemoteNodes
	 */
	public static RemoteNode mapSubtree(JSONObject obj) {
		JSONArray jsChildren = obj.optJSONArray("children");
		
		int len = 0;
		if (jsChildren != null) {
			len = jsChildren.length();
		}
		
		RemoteNode[] children = new RemoteNode[len];
		int maxChildHeight = -1;
		int numLeaves = len == 0 ? 1 : 0;
		
		for (int i = 0; i < len; i++) {
			JSONObject jsChild = null;
			try {
				jsChild = jsChildren.getJSONObject(i);
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RemoteNode child = mapSubtree(jsChild);
			children[i] = child;
			
			//note: height and numLeaves are fields in RemoteNode, so the tree is not actually traversed again for each of these.
			maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf()); 
			numLeaves += child.getNumberOfLeafNodes();
		}
		
		//create a RemoteNode for the current node
		String uuid = UUID.uuid();
		String label = obj.optString("name");
		label = label.length() > 0 ? label : children[0].getLabel();
		RemoteNode rNode = new RemoteNode(uuid, label, numLeaves, maxChildHeight + 1, children);
		
		return rNode;
	}
	
	public static JSONObject parseTree(String json) {
		//TODO use a streaming json parser for this if memory (or speed, probably) is an issue
		JSONObject root = null;
		try {
			JSONObject o = new JSONObject(json);
			root = o.getJSONObject("root");
		} catch (JSONException e) {
			System.err.println("Unable to parse tree " + json);
		}
		return root;
	}
}
