/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.model;

import java.util.Comparator;
import java.util.Set;

public interface INode
{
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

	public abstract double findMaximumDistanceToLeaf();

	public abstract String findLabelOfFirstLeafNode();

	public abstract void sortChildrenBy(Comparator<INode> comparator);

	public abstract Double getBranchLength();

	public abstract void setBranchLength(Double branchLength);
	
	public String getMetaDataString();

	/**
	 * @return the most recent common ancestor of the given set of nodes within this INode's subtree. Null
	 *         if this subtree does not contain all of the nodes.
	 */
	public abstract INode mrca(Set<INode> nodes);
}