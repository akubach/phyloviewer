/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.model;

import java.util.Comparator;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle;

public interface INode {
	
	public abstract int getId();
	
	public abstract void setId(int id);

	public abstract String getLabel();

	public abstract void setLabel(String label);

	public abstract int getNumberOfChildren();
	
	public abstract INode[] getChildren();

	public abstract INode getChild(int index);

	public abstract Boolean isLeaf();

	public abstract int getNumberOfLeafNodes();
	
	public abstract int getNumberOfNodes();

	public abstract int findMaximumDepthToLeaf();

	public abstract String findLabelOfFirstLeafNode();
	
	public abstract void sortChildrenBy(Comparator<INode> comparator);
	
	public abstract Object getData(String key);

	public abstract void setData(String string, Object data);
	
	public abstract INodeStyle getStyle();
	
	public abstract String getJSON();
}