package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public interface TreeData
{

	public abstract RemoteNode[] getChildren(String parentID);

	public abstract Tree getTree(String id);

	public abstract void addTree(Tree tree);
	
	/**
	 * Get a tree to at least the given depth.
	 * 
	 * @param depth the number of levels below the root to include
	 * @return the tree with the given id or null if that id is not found
	 */
	public abstract Tree getTree(String id, int depth);
	
	/**
	 * @param depth the number of levels below the root to include.  Use Integer.MAX_VALUE to guarantee getting the whole tree.
	 * @return the subtree rooted at rootID, to <strong>at least</strong> the given depth.
	 */
	public abstract RemoteNode getSubtree(String rootID, int depth);

	public abstract void addRemoteNode(RemoteNode node);

}