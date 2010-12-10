package org.iplantc.recon.server;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Node;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuildTreeFromJSON {

	public static Tree buildTree(JSONObject object) throws JSONException {
		Node root = buildNode(object.getJSONObject("tree").getJSONObject("root")); 
		Tree tree = new Tree();
		tree.setId(0);
		tree.setRootNode((INode)root);
		return tree;
	}
	
	public static Node buildNode(JSONObject object)  throws JSONException {
		int id = object.getInt("id");
		String name = object.optString("name");
		
		Node node = new Node(id,name);
		
		double branchLength = object.optDouble("branchLength");
		if(!Double.isNaN(branchLength)) {
			node.setBranchLength(branchLength);
		}
		
		JSONArray children = object.optJSONArray("children");
		if(null!=children) {
			int numChildren = children.length();
			Node[] myChildren = new Node[numChildren];
			for(int i = 0;i<numChildren;++i) {
				myChildren[i]=buildNode(children.getJSONObject(i));
			}
			
			node.setChildren(myChildren);
		}
		
		return node;
	}
}
