package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import java.util.ArrayList;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Node;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	//fields below will not be serialized
	private transient GotChildren gettingChildrenCallback = null;
	private static CombinedServiceAsync service;
	
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
	
	public static void setService(CombinedServiceAsync service) {
		RemoteNode.service = service;
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
	
	public void getChildrenAsync(final AsyncCallback<RemoteNode[]> callback) {
		
		if (this.getChildren() == null) 
		{
			if (this.gettingChildrenCallback == null) 
			{
				gettingChildrenCallback = new GotChildren(callback);	
				service.getChildren(this.getId(), gettingChildrenCallback);
			}
			else 
			{
				//there's already a request pending for the children. Add this callback to the list
				gettingChildrenCallback.otherCallbacks.add(callback);
			}
		} 
		else if (this.getChildren() != null && callback != null) 
		{
			callback.onSuccess((RemoteNode[])getChildren());
		}
	}
	
	private class GotChildren implements AsyncCallback<RemoteNode[]> {
		private ArrayList<AsyncCallback<RemoteNode[]>> otherCallbacks = new ArrayList<AsyncCallback<RemoteNode[]>>();
		
		public GotChildren(AsyncCallback<RemoteNode[]> callback)
		{
			otherCallbacks.add(callback);
		}
		
		/**
		 * Sets children field
		 * Subclasses must call this at some point in their onSuccess().
		 */
		@Override
		public void onSuccess(RemoteNode[] children) {
			RemoteNode.this.gettingChildrenCallback = null;
			RemoteNode.this.setChildren(children);
			
			//if there were other calls to getChildrenAsync while we were waiting for a response, do their callbacks
			for (AsyncCallback<RemoteNode[]> othercallback : otherCallbacks) {
				othercallback.onSuccess(children);
			}
			otherCallbacks.clear();
		}
		
		@Override
		public void onFailure(Throwable thrown) {
			RemoteNode.this.gettingChildrenCallback = null;
			thrown.printStackTrace();
			
			//if there were other calls to getChildrenAsync while we were waiting for a response, do their callbacks
			for (AsyncCallback<RemoteNode[]> othercallback : otherCallbacks) {
				othercallback.onFailure(thrown);
			}
			otherCallbacks.clear();
		}
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
}
