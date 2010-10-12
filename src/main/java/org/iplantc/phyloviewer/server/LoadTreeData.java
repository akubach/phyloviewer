package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;
import java.util.UUID;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCircular;
import org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCladogramHashMap;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.server.render.ImageGraphics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoadTreeData {
	
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
	
	public static BufferedImage renderTreeImage(Tree tree, ILayout layout,
			int width, int height) {

		ImageGraphics graphics = new ImageGraphics(width, height);

		RenderTreeCladogram renderer = new RenderTreeCladogram();
		renderer.setCollapseOverlaps(false);
		renderer.setDrawLabels(false);

		renderer.renderTree(tree, layout, graphics, null, null);

		return graphics.getImage();
	}
	
	public static void loadTreeDataFromJSON(String id, String json, ITreeData treeData, ILayoutData layoutData, IOverviewImageData iOverviewImageData ) {
		JSONObject root = parseTree(json);

		RemoteNode remoteRoot = mapSubtree(root);
		
		Tree tree = new Tree();
		tree.setId(id);
		tree.setRootNode(remoteRoot);
		
		treeData.addTree(tree);
		
		{
			LayoutCircular layout = new LayoutCircular(0.5);
			layout.layout(tree);
			String uuid = UUID.randomUUID().toString();
			
			layoutData.putLayout(uuid, layout, tree);
		}
		
		{
			LayoutCladogram layout = new LayoutCladogramHashMap(0.8,1.0);
			layout.layout(tree);
			String uuid = UUID.randomUUID().toString();
			
			layoutData.putLayout(uuid, layout, tree);
			
			iOverviewImageData.setOverviewImage(id, uuid, renderTreeImage(tree,layout, 256,1024));
		}
	}
}
