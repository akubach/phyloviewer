package org.iplantc.phyloviewer.server;

import java.util.UUID;

import org.iplantc.phyloviewer.client.DemoTree;
import org.iplantc.phyloviewer.client.FetchTree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RemoteNodeServiceImpl extends RemoteServiceServlet implements RemoteNodeService {
	private static final long serialVersionUID = 3050278763811296728L;
	private TreeData treeData;

	public void init() {
		System.out.println("Starting RemoteNodeServiceImpl");
		this.getServletContext().setAttribute("org.iplantc.phyloviewer.server.RemoteNodeServiceImpl", this);
		
//		treeData = new JavaTreeData(); 
		treeData = new DatabaseTreeData(this.getServletContext());
		
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
		return treeData.getChildren(parentID);
	}
	
	@Override
	public Tree getTree(DemoTree demoTree) {
		return getTree(demoTree.id);
	}

	@Override
	public Tree getTree(String id) 
	{
		Tree tree;
		
		synchronized (id) //TODO id is probably not a good lock
		{ 
			tree = treeData.getTree(id, 0);
		
			//this may be the id for a demo tree thats not loaded yet
			if (tree == null) 
			{
				DemoTree demotree = DemoTree.byID(id);
				if (demotree != null) 
				{
					tree = fetchTree(demotree);
					treeData.addTree(tree);
				}
			}
		}
		
		return tree;
	}
	
	public Tree getTree(String id, int depth) {
		return treeData.getTree(id, depth);
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
		int numNodes = 1;
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
			
			//note: numNodes, height and numLeaves are fields in RemoteNode, so the tree is not actually traversed again for each of these.
			maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf()); 
			numLeaves += child.getNumberOfLeafNodes();
			numNodes += child.getNumberOfNodes();
		}
		
		//create a RemoteNode for the current node
		String uuid = UUID.randomUUID().toString();
		String label = obj.optString("name");
		label = label.length() > 0 ? label : children[0].getLabel();
		RemoteNode rNode = new RemoteNode(uuid, label, numNodes, numLeaves, maxChildHeight + 1, children);
		
		return rNode;
	}
	
	public static JSONObject parseTree(String json) {
		//TODO use a streaming json parser for this if memory (or speed, probably) is an issue
		JSONObject root = null;
		try {
			JSONObject o = new JSONObject(json);
			root = o.getJSONObject("root");
		} catch (JSONException e) {
			System.err.println("Unable to parse tree");
		}
		return root;
	}
	
	private Tree fetchTree(DemoTree demoTree) {
		Tree tree;
		String json = getFetchTree().fetchTree(demoTree);

		JSONObject root = parseTree(json);

		RemoteNode remoteRoot = mapSubtree(root);
		tree = new Tree();
		tree.setId(demoTree.id);
		tree.setRootNode(remoteRoot);
		return tree;
	}
}
