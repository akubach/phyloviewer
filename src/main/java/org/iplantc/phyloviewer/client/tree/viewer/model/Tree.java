package org.iplantc.phyloviewer.client.tree.viewer.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Tree implements ITree, IsSerializable {
	private INode root;
	private String id;

	@Override
	public String getJSON() {
		String json = "{\"root\":";
		json += root == null ? "null" : this.getRootNode().getJSON();
		json += "}";

		return json;
	}

	@Override
	public int getNumberOfNodes() {
		return root.getNumberOfNodes();
	}

	@Override
	public INode getRootNode() {
		return root;
	}

	@Override
	public void setRootNode(INode node) {
		this.root = node;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof Tree))
		{
			return false;
		}

		Tree that = (Tree)obj;
		return this.id.equals(that.getId()) && this.root.equals(that.getRootNode());
	}
}
