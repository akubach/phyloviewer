package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public interface TreeData
{

	public abstract RemoteNode[] getChildren(String parentID);

	public abstract Tree getTree(String id);

	public abstract void addTree(Tree tree);

	public abstract void addRemoteNode(RemoteNode node);

}