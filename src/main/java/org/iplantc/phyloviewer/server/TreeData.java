package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public interface TreeData
{
	/**
	 * Adds a tree. If a tree with the same id already exists, it is replaced.
	 * @param tree
	 */
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

	/**
	 * Gets the children of the parent node with id parentID. (The order of the children is not defined
	 * in the current implementation.)
	 * 
	 * @param parentID
	 * @return the children of parentID.  Null if the node had no children
	 */
	public abstract RemoteNode[] getChildren(String parentID);
}
