/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

public interface ITree {

	public abstract void setRootNode(INode node);

	public abstract INode getRootNode();

	public abstract int getNumberOfNodes();

	public abstract String getJSON();

	public String getId();
}