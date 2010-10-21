package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout.GotLayouts;
import org.iplantc.phyloviewer.client.tree.viewer.model.Node;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.IStyleMap;
import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteNode extends Node implements IsSerializable {

	private int numNodes;
	private int numLeaves;
	private int height;
	private int numChildren;
	
	//fields below will not be serialized
	private transient boolean gettingChildren = false;
	private static CombinedServiceAsync service;
	private static IStyleMap styleMap;
	
	
	public RemoteNode(int id,String label, int numNodes, int numLeaves, int height, RemoteNode[] children) {
		this(id, label, numNodes, numLeaves, height, children.length);
		super.setChildren(children);
	}
	
	/**
	 * Creates a node without children.  Children will be fetched later by the client, using getChildrenAsync()
	 */
	public RemoteNode(int id,String label, int numNodes, int numLeaves, int height, int numChildren) {
		super(id, label);
		this.numLeaves = numLeaves;
		this.height = height;
		this.numChildren = numChildren;
		this.numNodes = numNodes;
	}
	
	/** no-arg constructor required for serialization */
	public RemoteNode() { 
	}
	
	public static void setService(CombinedServiceAsync service) {
		RemoteNode.service = service;
	}
	
	public static void setStyleMap(IStyleMap styleMap) {
		RemoteNode.styleMap = styleMap;
	}
	
	@Override
	public int getNumberOfLeafNodes() {
		return numLeaves;
	}
	
	@Override
	public String findLabelOfFirstLeafNode() {
		return getLabel();
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
	
	public void getChildrenAsync(final GotChildren callback) {
		if (this.getChildren() == null && !gettingChildren) {
			gettingChildren = true;
			
			//TODO find a way to batch these requests, in case several nodes want to get children during the same frame render
			service.getChildren(this.getId(), callback);
			
		} else if (this.getChildren() != null && callback != null) {
			callback.onSuccess((RemoteNode[])this.getChildren());
		}

		//TODO handle (this.children == null && gettingChildren)
	}
	
	public class GotChildren implements AsyncCallback<RemoteNode[]> {
		
		/**
		 * Sets children field and unsets gettingChildren flag
		 * Subclasses must call this at some point in their onSuccess().
		 */
		@Override
		public void onSuccess(RemoteNode[] children) {
			gettingChildren = false;
			RemoteNode.this.setChildren(children);
			styleMap.styleSubtree(RemoteNode.this);
		}
		
		@Override
		public void onFailure(Throwable thrown) {
			// TODO handle this
			gettingChildren = false;
			thrown.printStackTrace();
		}
	}

	/**
	 * A GotChildren callback that also fetches layouts. It waits for the
	 * layouts to be returned before setting this RemoteNode's children and
	 * requesting a render.
	 */
	public abstract class GotChildrenGetLayouts extends GotChildren {
		RemoteLayout layout;
		
		public GotChildrenGetLayouts(RemoteLayout layout) {
			this.layout = layout;
		}
		
		@Override
		public void onSuccess(final RemoteNode[] children) {
			
			GotLayouts gotLayouts = layout.new GotLayouts() {
				@Override
				public void gotLayouts(LayoutResponse[] responses) {
					GotChildrenGetLayouts.super.onSuccess(children);
					gotChildrenAndLayouts();
				}
			};
			
			layout.getLayoutAsync(children, gotLayouts);
		}
		
		public abstract void gotChildrenAndLayouts();
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
}
