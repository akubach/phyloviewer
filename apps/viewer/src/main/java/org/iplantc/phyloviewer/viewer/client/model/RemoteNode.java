package org.iplantc.phyloviewer.viewer.client.model;

import java.util.Set;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Node;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteNode extends Node implements IsSerializable {

	private int numChildren;
	private int numNodes;
	private int numLeaves;
	private int height;
	private int depth;
	
	/** any node (in the same tree) with a leftIndex >= this.leftIndex and rightIndex <= this.rightIndex is in this node's subtree */
	private int leftIndex;
	private int rightIndex;

	/**
	 * Creates a node without children. Children can be added with setChildren(), or be fetched (on the
	 * client), using getChildrenAsync()
	 */
	public RemoteNode(int id, String label, int numChildren, int numNodes, int numLeaves, int depth, int height, int leftIndex, int rightIndex) {
		super(id, label);
		//TODO do some validation on these
		this.numChildren = numChildren;
		this.numNodes = numNodes;
		this.numLeaves = numLeaves;
		this.depth = depth;
		this.height = height;
		this.leftIndex = leftIndex;
		this.rightIndex = rightIndex;
	}
	
	/** no-arg constructor required for serialization */
	public RemoteNode() { 
	}
	
	@Override
	public int getNumberOfLeafNodes() {
		return numLeaves;
	}
	
	@Override
	public String findLabelOfFirstLeafNode() {
		if(numChildren==0) {
			return getLabel();
		}
		return this.getChild(0).findLabelOfFirstLeafNode();
	}

	@Override
	public int findMaximumDepthToLeaf() {
		return height;
	}

	/**
	 * @return the number of children this node has. These children may not have been fetched yet.
	 */
	@Override
	public int getNumberOfChildren() {
		return numChildren;
	}
		
	@Override
	public int getNumberOfNodes()
	{
		return numNodes;
	}
	
	public int getNumberOfLocalNodes() 
	{
		int count = 1;
		
		if (getChildren() != null) 
		{
			for (INode child : getChildren()) 
			{
				if (child instanceof RemoteNode)
				{
					count += ((RemoteNode)child).getNumberOfLocalNodes();
				}
				else
				{
					count += child.getNumberOfNodes();
				}
			}
		}
		
		return count;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof RemoteNode))
		{
			return false;
		}

		RemoteNode that = (RemoteNode)obj;

		return super.shallowEquals(that)
			&& this.numChildren == that.getNumberOfChildren()
			&& this.numLeaves == that.getNumberOfLeafNodes()
			&& this.height == that.findMaximumDepthToLeaf()
			&& this.numNodes == that.getNumberOfNodes();
	}
	
	@Override
	public int hashCode()
	{
		return getId();
	}

	@Override
	public void setChildren(Node[] children) 
	{
		/*
		 * TODO Adding non-RemoteNode children invalidates the topology fields (right/left indices, height, etc). Make sure children
		 * are RemoteNodes with the correct topology fields
		 */
		super.setChildren(children);
		if (children != null)
		{
			this.numChildren = children.length;
		}
	}
	
	public boolean subtreeContains(int traversalIndex)
	{
		return traversalIndex >= leftIndex && traversalIndex <= rightIndex;
	}
	
	public boolean subtreeContains(RemoteNode node)
	{
		return subtreeContains(node.getLeftIndex());
	}
	
	/** any node (in the same tree) with a leftIndex >= this.leftIndex and rightIndex <= this.rightIndex is in this node's subtree */
	public int getLeftIndex() 
	{
		return leftIndex;
	}
	
	/** any node (in the same tree) with a leftIndex >= this.leftIndex and rightIndex <= this.rightIndex is in this node's subtree */
	public int getRightIndex()
	{
		return rightIndex;
	}
	
	public int getDepth()
	{
		return depth;
	}

	@Override
	public RemoteNode getChild(int index)
	{
		return (RemoteNode) super.getChild(index);
	}

	@Override
	public RemoteNode[] getChildren()
	{
		if(super.getChildren() == null) {
			return null;
		}
		
		int numChildren = this.getNumberOfChildren();
		RemoteNode[] array = new RemoteNode[numChildren];
		for(int i = 0; i < numChildren; ++i) {
			array[i] = this.getChild(i);
		}
		return array;
	}	
	
	@Override
	public RemoteNode mrca(Set<INode> nodes)
	{
		if (nodes == null || nodes.isEmpty())
		{
			return null;
		}
		
		int minLeft = Integer.MAX_VALUE;
		int maxRight = Integer.MIN_VALUE;
		for (INode node : nodes)
		{
			RemoteNode rNode = (RemoteNode)node;
			minLeft = Math.min(minLeft, rNode.getLeftIndex());
			maxRight = Math.max(maxRight, rNode.getRightIndex());
		}
		
		RemoteNode mrca = localMRCA(minLeft, maxRight);
		return mrca;
	}

	public RemoteNode localMRCA(int minLeft, int maxRight)
	{
		RemoteNode mrca = null;
		
		if (this.leftIndex <= minLeft && this.rightIndex >= maxRight)
		{
			/*
			 * For localMRCA(), assuming MRCA is this node if the children haven't been fetched, so that
			 * the method doesn't have to go async. Obviously this is not generally true. TODO write a
			 * real async MRCA function that checks on the server.
			 */
			mrca = this;  
			
			for (RemoteNode child : this.getChildren())
			{
				RemoteNode childMRCA = child.localMRCA(minLeft, maxRight);
				if (childMRCA != null)
				{
					mrca = childMRCA;
				}
			}
		}
		
		return mrca;
	}
}
