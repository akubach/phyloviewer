package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.Comparator;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;

public interface INode {

	public abstract String getLabel();

	public abstract void setLabel(String label);

	public abstract int getNumberOfChildren();

	public abstract INode getChild(int index);

	public abstract Vector2 getPosition();

	public abstract void setPosition(Vector2 position);

	public abstract Box2D getBoundingBox();

	public abstract void setBoundingBox(Box2D boundingBox);

	public abstract Boolean isLeaf();

	public abstract int getNumberOfLeafNodes();

	public abstract int findMaximumDepthToLeaf();

	public abstract String findLabelOfFirstLeafNode();
	
	public abstract void sortChildrenBy(Comparator<INode> comparator);

}