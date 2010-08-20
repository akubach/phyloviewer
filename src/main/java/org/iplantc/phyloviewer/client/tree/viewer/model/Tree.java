package org.iplantc.phyloviewer.client.tree.viewer.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Tree implements ITree, IsSerializable {
	private int numNodes;
	private INode root;

	@Override
	public String getJSON() {
		String json = "{\"root\":";
		json += root == null ? "null" : this.getRootNode().getJSON();
		json += "}";

		return json;
	}

	@Override
	public int getNumberOfNodes() {
		return numNodes;
	}

	@Override
	public INode getRootNode() {
		return root;
	}

	@Override
	public void setRootNode(INode node) {
		this.root = node;
		this.numNodes = Tree.countNumberOfNodes(node);
	}

	public static final int countNumberOfNodes ( INode node ) {
		int count = 1;
		if (!node.isLeaf()) {
			for ( int i = 0; i < node.getNumberOfChildren(); ++i ) {
				count += Tree.countNumberOfNodes(node.getChild(i));
			}
		}
		return count;
	}

}
