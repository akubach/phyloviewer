package org.iplantc.phyloviewer.server;

import java.util.concurrent.ConcurrentHashMap;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public class JavaTreeData implements TreeData
{
	private static final ConcurrentHashMap<String,RemoteNode> nodes = new ConcurrentHashMap<String,RemoteNode>();
	private static final ConcurrentHashMap<String,Tree> trees = new ConcurrentHashMap<String,Tree>();

	@Override
	public void addTree(Tree tree)
	{
		trees.put(tree.getId(), tree);
	}

	@Override
	public void addRemoteNode(RemoteNode node)
	{
		nodes.put(node.getUUID(), node);
	}

	@Override
	public RemoteNode[] getChildren(String parentID)
	{
		return nodes.get(parentID).getChildren();
	}

	@Override
	public Tree getTree(String id)
	{
		return trees.get(id);
	}

}
