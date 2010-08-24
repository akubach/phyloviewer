package org.iplantc.phyloviewer.server;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

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
	private FetchTree fetchTree;
	private HashMap<String, RemoteNode> localSessionNodes = new HashMap<String, RemoteNode>(); //for junit test

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
		return getSessionNodeMap().get(parentID).getChildren();
	}
	
	@Override
	public Tree fetchTree(int i) {
		String json = fetchTree.fetchTree(i);

		//TODO use a streaming json parser for this if memory (or speed, probably) is an issue
		JSONObject root = null;
		try {
			JSONObject o = new JSONObject(json);
			root = o.getJSONObject("root");
		} catch (JSONException e) {
			System.err.println("Unable to parse tree " + json);
		}

		RemoteNode remoteRoot = mapSubtree(root);
		Tree tree = new Tree();
		tree.setRootNode(remoteRoot);
		
		//TODO cache the tree object
		
		return tree;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, RemoteNode> getSessionNodeMap() {
		if (this.getThreadLocalRequest() != null) {
			HttpSession session = this.getThreadLocalRequest().getSession();
			
			if (session.getAttribute("nodes") == null) {
				session.setAttribute("nodes", new HashMap<UUID, RemoteNode>());
			}
			
			return (HashMap<String, RemoteNode>) session.getAttribute("nodes");
		} else {
			return localSessionNodes;
		}
	}
	
	void addRemoteNode(RemoteNode node) {
		getSessionNodeMap().put(node.getUUID(), node);
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
		RemoteNode rNode = new RemoteNode(uuid, label, numLeaves, maxChildHeight + 1);
		rNode.setChildren(children);
		RemoteNodeServiceImpl.this.addRemoteNode(rNode);
		
		return rNode;
	}
}
