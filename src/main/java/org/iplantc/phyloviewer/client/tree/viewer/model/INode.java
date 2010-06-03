package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.Comparator;

public interface INode {

	public abstract String getLabel();

	public abstract void setLabel(String label);

	public abstract int getNumberOfChildren();

	public abstract INode getChild(int index);

	public abstract String getNodeColor();
	
	public abstract void setNodeColor(String color);

	public abstract Boolean isLeaf();

	public abstract int getNumberOfLeafNodes();

	public abstract int findMaximumDepthToLeaf();

	public abstract String findLabelOfFirstLeafNode();
	
	public abstract void sortChildrenBy(Comparator<INode> comparator);

}