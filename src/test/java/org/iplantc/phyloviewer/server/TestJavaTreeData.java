package org.iplantc.phyloviewer.server;

import static org.junit.Assert.*;

import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.junit.Before;
import org.junit.Test;

public class TestJavaTreeData
{
	JavaTreeData treeData;
	RemoteNode child0;
	RemoteNode child1;
	RemoteNode parent;
	Tree tree;

	@Before
	public void setUp()
	{
		treeData = new JavaTreeData();
		
		child0 = new RemoteNode("child0ID", "", 1, 1, 0, 0);
		child1 = new RemoteNode("child1ID", "", 1, 1, 0, 0);
		RemoteNode[] children = new RemoteNode[] { child0, child1 };
		parent = new RemoteNode("parentID", "", 3, 2, 1, children);

		treeData.addRemoteNode(parent);
		treeData.addRemoteNode(child0);
		treeData.addRemoteNode(child1);

		tree = new Tree();
		tree.setId("treeID");
		tree.setRootNode(parent);
		treeData.addTree(tree,"");
	}

	@Test
	public void testGetChildren()
	{
		RemoteNode[] returnedChildren = treeData.getChildren("parentID");
		assertNotNull(returnedChildren);
		assertEquals(2, returnedChildren.length);
		assertEquals(child0, returnedChildren[0]);
		assertEquals(child1, returnedChildren[1]);

		returnedChildren = treeData.getChildren("child0ID");
		assertNull(returnedChildren);

		returnedChildren = treeData.getChildren("child1ID");
		assertNull(returnedChildren);
	}

	@Test
	public void testGetTree()
	{
		Tree returnedTree = treeData.getTree("treeID", 0);
		assertEquals(tree, returnedTree);
	}

}
