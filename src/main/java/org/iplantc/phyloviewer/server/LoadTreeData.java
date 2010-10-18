package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCircular;
import org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCladogramHashMap;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.server.render.ImageGraphics;

public class LoadTreeData {
	
	public static BufferedImage renderTreeImage(Tree tree, ILayout layout,
			int width, int height) {

		ImageGraphics graphics = new ImageGraphics(width, height);

		RenderTreeCladogram renderer = new RenderTreeCladogram();
		renderer.setCollapseOverlaps(false);
		renderer.setDrawLabels(false);

		renderer.renderTree(tree, layout, graphics, null, null);

		return graphics.getImage();
	}

	public static int loadTreeData(RemoteNode root, String name, ITreeData treeData, ILayoutData layoutData, IOverviewImageData iOverviewImageData ) {
		
		Tree tree = new Tree();
		tree.setRootNode(root);
		
		treeData.addTree(tree,name);
		
		{
			LayoutCircular layout = new LayoutCircular(0.5);
			layout.layout(tree);
			String uuid = layout.getType().toString();
			
			layoutData.putLayout(uuid, layout, tree);
		}
		
		{
			LayoutCladogram layout = new LayoutCladogramHashMap(0.8,1.0);
			layout.layout(tree);
			String uuid = layout.getType().toString();
			
			layoutData.putLayout(uuid, layout, tree);
			
			iOverviewImageData.setOverviewImage(tree.getId(), uuid, renderTreeImage(tree,layout, 256,1024));
		}
		
		return tree.getId();
	}
	
	public static int loadTreeDataFromNewick(String newick, String name, ServletContext context) {
		
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		FileData data = null;
		try {
			data = parser.parse(newick);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		org.iplantc.phyloparser.model.Tree tree = null;
		
		List<Block> blocks = data.getBlocks();
		for ( Block block : blocks ) {
			if ( block instanceof TreesBlock ) {
				TreesBlock trees = (TreesBlock) block;
				tree = trees.getTrees().get( 0 );
			}
		}
		
		ITreeData treeData = (ITreeData) context.getAttribute(Constants.TREE_DATA_KEY);
		ILayoutData layoutData = (ILayoutData) context.getAttribute(Constants.LAYOUT_DATA_KEY);
		IOverviewImageData overviewData = (IOverviewImageData) context.getAttribute(Constants.OVERVIEW_DATA_KEY);
		
		RemoteNode root = convertDataModels(tree.getRoot());
		
		return loadTreeData(root,name,treeData,layoutData,overviewData);
	}
	
	private static RemoteNode convertDataModels(org.iplantc.phyloparser.model.Node node) {
		
		List<Node> myChildren = node.getChildren();
		
		int len = myChildren.size();
		RemoteNode[] children = new RemoteNode[len];
		int numNodes = 1;
		int maxChildHeight = -1;
		int numLeaves = len == 0 ? 1 : 0;
		
		for (int i = 0; i < len; i++) {
			Node myChild = myChildren.get(i);
			
			RemoteNode child = convertDataModels(myChild);
			children[i] = child;
			
			//note: numNodes, height and numLeaves are fields in RemoteNode, so the tree is not actually traversed again for each of these.
			maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf()); 
			numLeaves += child.getNumberOfLeafNodes();
			numNodes += child.getNumberOfNodes();
		}
		
		//create a RemoteNode for the current node
		String label = node.getName();

		if(null == label || label.isEmpty() ) {
			label = ( children != null ? children[0].getLabel() : "" );
		}
		RemoteNode rNode = new RemoteNode(0, label, numNodes, numLeaves, maxChildHeight + 1, children);
		
		return rNode;
	}
}
