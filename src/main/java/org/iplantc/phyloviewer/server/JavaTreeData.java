package org.iplantc.phyloviewer.server;

import java.util.concurrent.ConcurrentHashMap;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

public class JavaTreeData implements ITreeData
{
	private static final ConcurrentHashMap<String,RemoteNode> nodes = new ConcurrentHashMap<String,RemoteNode>();
	private static final ConcurrentHashMap<String,Tree> trees = new ConcurrentHashMap<String,Tree>();

	@Override
	public void addTree(Tree tree,String name)
	{
		trees.put(tree.getId(), tree);
		RemoteNode root = (RemoteNode)tree.getRootNode();
		addSubtree(root);
	}
	
	public void addRemoteNode(RemoteNode node)
	{
		nodes.put(node.getUUID(), node);
	}
	
	@Override
	public RemoteNode[] getChildren(String parentID)
	{
		return getSubtree(parentID, 1).getChildren();
	}
	
	@Override
	public RemoteNode getSubtree(String rootID, int depth)
	{
		return nodes.get(rootID);
	}

	@Override
	public Tree getTree(String id, int depth)
	{
		return trees.get(id);
	}
	
	private void addSubtree(RemoteNode node) {
		addRemoteNode(node);
		
		if (!node.isLeaf()) {
			for (RemoteNode child : node.getChildren()) {
				addSubtree(child);
			}
		}
	}

	@Override
	public String getTrees() {
		// TODO Auto-generated method stub
		return null;
	}
}
