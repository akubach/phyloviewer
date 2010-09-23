package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import java.util.Arrays;
import java.util.Comparator;

import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout.GotLayouts;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteNode implements INode, IsSerializable {

	private String uuid;
	private String label;
	private int numLeaves;
	private int height;
	private int numChildren;
	
	//will not be serialized
	private transient RemoteNode[] children; 
	private transient boolean gettingChildren = false;
	private static RemoteNodeServiceAsync service;
	static int nodeCount = 0;
	
	public RemoteNode(String uuid, String label, int numLeaves, int height, RemoteNode[] children) {
		this.uuid = uuid;
		this.label = label;
		this.numLeaves = numLeaves;
		this.height = height;
		this.children = children; 			//will not be serialized
		this.numChildren = children.length; //will be serialized
	}
	
	/** no-arg constructor required for serialization */
	public RemoteNode() { 
		nodeCount++;
	}
	
	public static void setService(RemoteNodeServiceAsync service) {
		//TODO try to inject the service automatically
		RemoteNode.service = service;
	}
	
	
	/*
	 * Methods that do not trigger a fetch
	 */
	
	@Override
	public int getId() {
		throw new UnsupportedOperationException();
	}
	
	public String getUUID() {
		return uuid;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public Boolean isLeaf() {
		return height == 0;
	}
	
	@Override
	public int getNumberOfLeafNodes() {
		return numLeaves;
	}
	
	@Override
	public String findLabelOfFirstLeafNode() {
		return label;
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

	/**
	 * @throws ArrayIndexOutOfBoundsException if index is out of bounds
	 * @throws RuntimeException if children haven't been fetched
	 */
	@Override
	public RemoteNode getChild(int index) {
		if (children == null) {
//			throw new RuntimeException("Node " + uuid.toString() + " doesn't have its children yet");
			return null;
		} else if (index < 0 || index >= children.length) {
			throw new ArrayIndexOutOfBoundsException("Child #" + index + " does not exist.");
		}
		
		return children[index];
	}
	
	@Override
	public String getJSON() {
		String json = "{\"name\":\"" + this.getLabel() + "\",\"children\":[";
		
		if (children != null) {
			for (int i = 0, len = this.getNumberOfChildren(); i < len; i++) {
				json += this.getChild(i).getJSON();
				if (i < len - 1) {
					json += ",";
				}
			}
		}
		
		json += "]}";
		
		return json;
	}
	
	/*
	 * Methods that trigger one fetch 
	 */
	
	public void getChildrenAsync(final GotChildren callback) {
		if (this.children == null && !gettingChildren) {
			gettingChildren = true;
			
			//TODO find a way to batch these requests, in case several nodes want to get children during the same frame render
			service.getChildren(this.getUUID(), callback);
			
		} else if (this.children != null && callback != null) {
			callback.onSuccess(children);
		}

		//TODO handle (this.children == null && gettingChildren)
	}
	
	/**
	 * May be null if children haven't been fetched yet.
	 */
	public RemoteNode[] getChildren() {
		return this.children;
	}
	
	/*
	 * Methods that may trigger a fetch every time
	 */
	
	@Override
	public Object getData(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INodeStyle getStyle() {
		//TODO figure out how i'm going to deal with styling
		
		return new INodeStyle() {
			@Override
			public IElementStyle getElementStyle(Element element) {
				return new IElementStyle() {
					public void setStrokeColor(String color) { }
					public void setLineWidth(double width) { }
					public void setFillColor(String color) { }
					public String getStrokeColor() {return "black";}
					public double getLineWidth() {return 1.0;}
					public String getFillColor() {return "black";}
				};
			}
		};
	}

	

	/*
	 * Methods that may have to write back to the servlet
	 */

	@Override
	public void setData(String string, Object data) {
		//TODO setData
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void sortChildrenBy(final Comparator<INode> comparator) {
		if (children != null) {
			Arrays.sort(RemoteNode.this.children, comparator);
		} 
	}
	
	public class GotChildren implements AsyncCallback<RemoteNode[]> {
		
		/**
		 * Sets children field and unsets gettingChildren flag
		 * Subclasses must call this at some point in their onSuccess().
		 */
		@Override
		public void onSuccess(RemoteNode[] children) {
			gettingChildren = false;
			RemoteNode.this.children = children;
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
				protected void gotLayouts(LayoutResponse[] responses) {
					GotChildrenGetLayouts.super.onSuccess(children);
					gotChildrenAndLayouts();
				}
			};
			
			layout.getLayoutAsync(children, gotLayouts);
		}
		
		public abstract void gotChildrenAndLayouts();
	}
}
