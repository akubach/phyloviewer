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
	private FetchTree fetchTree;

	public RemoteNodeServiceImpl() {
		fetchTree = new FetchTreeImpl();
	}
	
	public void setFetchTree(FetchTree fetchTree) {
		this.fetchTree = fetchTree;
	}
	
	@Override
	public RemoteNode[] getChildren(String parentID) {
		//note: the children will be serialized *without* their children (which is a transient field)
		System.out.println("Sending children of " + parentID);
		return nodes.get(parentID).getChildren();
	}
	
	@Override
	public Tree fetchTree(int i) {
		String json = fetchTree.fetchTree(i);

		JSONObject root = parseTree(json);

		RemoteNode remoteRoot = mapSubtree(root);
		Tree tree = new Tree();
		tree.setId(remoteRoot.getUUID());
		tree.setRootNode(remoteRoot);
		addTree(tree);
		
		return tree;
	}

	@Override
	public Tree fetchTree(String id) {
		return trees.get(id);
	}
	
	private void addTree(Tree tree) {
		trees.put(tree.getId(), tree);
	}
	
	void addRemoteNode(RemoteNode node) {
		nodes.put(node.getUUID(), node);
	}
	
	RemoteNode mapSubtree(JSONObject obj) {
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
		RemoteNodeServiceImpl.this.addRemoteNode(rNode);
		
		return rNode;
	}
	
	private JSONObject parseTree(String json) {
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
