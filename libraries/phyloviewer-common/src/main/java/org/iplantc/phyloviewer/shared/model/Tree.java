package org.iplantc.phyloviewer.shared.model;


public class Tree implements ITree {
	private INode root;
	int id;
	
	public Tree() {
	}

	public Tree(int id, INode root) {
		this.setRootNode(root);
		this.id = id;
	}

	@Override
	public String getJSON() {
		String json = "{\"id\":" + this.getId() + ", \"root\":";
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
		return this.id == that.getId() && this.root.equals(that.getRootNode());
	}
}
