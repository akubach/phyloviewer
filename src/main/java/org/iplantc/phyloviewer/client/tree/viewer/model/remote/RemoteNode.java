package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import java.util.Arrays;
import java.util.Comparator;

import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteNode implements INode, IsSerializable {
	public static final int CHILDREN_NOT_FETCHED = -1;
	
	private int id; //note to self, for now ID will stay, but it looks like id is only used to index layout Vectors, so if I do layout on the server, id isn't really needed anymore.  Would rather use UUIDs for servlet.
	private String uuid;
	private String label;
	private int numLeaves;
	private int height;
	
	//will not be serialized
	private transient RemoteNode[] children; 
	private transient boolean gettingChildren = false;
	private static RemoteNodeServiceAsync service;
	
	public RemoteNode(String uuid, String label, int numLeaves, int height, RemoteNode[] children) {
		this.uuid = uuid;
		this.label = label;
		this.numLeaves = numLeaves;
		this.height = height;
		this.children = children;
	}
	
	/** no-arg constructor required for serialization */
	public RemoteNode() { }
	
	public static void setService(RemoteNodeServiceAsync service) {
		//TODO try to inject the service automatically
		RemoteNode.service = service;
	}
	
	
	/*
	 * Methods that do not trigger a fetch
	 */
	
	@Override
	public int getId() {
		return id;
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
	 * @return the number of children this node has. Returns
	 *         {@link RemoteNode#CHILDREN_NOT_FETCHED} until children have been fetched from the
	 *         remote tree.
	 */
	@Override
	public int getNumberOfChildren() {
		if (children == null) {
			return CHILDREN_NOT_FETCHED;
		}
		return children.length;
	}

	/**
	 * @throws ArrayIndexOutOfBoundsException if index is out of bounds
	 * @throws RuntimeException if children haven't been fetched
	 */
	@Override
	public RemoteNode getChild(int index) {
		if (children == null) {
			throw new RuntimeException("Node " + uuid.toString() + " doesn't have its children yet");
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
			callback.afterSetChildren(this.children);
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
		//TODO
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
		/** optional subclass method */
		protected void beforeSetChildren(RemoteNode[] children) {}
		
		/** optional subclass method */
		protected void afterSetChildren(RemoteNode[] children) {}
		
		@Override
		public final void onSuccess(RemoteNode[] children) {
			gettingChildren = false;
			System.out.println("Received children of " + RemoteNode.this.uuid);
			
			this.beforeSetChildren(children);
			RemoteNode.this.children = children;
			this.afterSetChildren(children);
		}
		
		@Override
		public void onFailure(Throwable thrown) {
			// TODO handle this
			gettingChildren = false;
			thrown.printStackTrace();
		}
	}
}
