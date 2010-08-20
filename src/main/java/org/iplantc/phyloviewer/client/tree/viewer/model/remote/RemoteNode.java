package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import java.util.Arrays;
import java.util.Comparator;

import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteNode implements INode, IsSerializable {
	private int id; //note to self, for now ID will stay, but it looks like id is only used to index layout Vectors, so if I do layout on the server, id isn't really needed anymore.  Would rather use UUIDs for servlet.
	private String uuid;
	private String label;
	private int numLeaves;
	private int height;
	
	//will not be serialized
	private transient RemoteNode[] children; 
	private transient boolean gettingChildren = false;
	private static RemoteNodeServiceAsync service;
	
	public RemoteNode(String uuid, String label, int numLeaves, int height) {
		this.uuid = uuid;
		this.label = label;
		this.numLeaves = numLeaves;
		this.height = height;
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
	
	/*
	 * Methods that will trigger a fetch once
	 */
	
	/**
	 * Will be Integer.MAX_VALUE until children have been fetched
	 */
	@Override
	public int getNumberOfChildren() {
		if (children == null) {
			getChildrenAsync(null);
			TreeWidget.instance.requestRender(); //FIXME a hacky way of triggering a render
			return Integer.MAX_VALUE; //FIXME a hacky way of preventing the renderer from trying to get children.  TODO Update renderers to deal with async nodes.
		}
		return children.length;
	}

	/**
	 * Will throw an exception if children haven't been fetched
	 */
	@Override
	public RemoteNode getChild(int index) {
		if (children != null) {
			return children[index];
		} else {
			//this shouldn't have been called without checking getNumberOfChildren() first
			throw new RuntimeException("Node " + uuid.toString() + " doesn't have its children yet");
		}
	}
	
	public void getChildAsync(final int index, final AsyncCallback<RemoteNode> callback) {
		getChildrenAsync(new ChildrenCallback() {
			public void onSuccess() {
				callback.onSuccess(RemoteNode.this.children[index]);
			}
			public void onFailure() { }
		});
	}
	
	public void getChildrenAsync(final ChildrenCallback callback) {
		if (this.children == null && !gettingChildren) {
			gettingChildren = true;
			
			//TODO find a way to batch these requests, in case several nodes want to get children during the same frame render
			service.getChildren(this.uuid, new AsyncCallback<RemoteNode[]>() {
				
				@Override
				public void onSuccess(RemoteNode[] children) {
					gettingChildren = false;
					System.out.println("Received children of " + RemoteNode.this.uuid);
					
					RemoteNode.this.children = children;
					
					if (callback != null) {
						callback.onSuccess();
					}
				}

				@Override public void onFailure(Throwable arg0) { 
					gettingChildren = false;
				}
			});
			
		} else if (callback != null) {
			callback.onSuccess();
		}
	}
	
	/**
	 * May be null if children haven't been fetched yet.  Will trigger (asynchronous) fetch of children in that case.
	 */
	public RemoteNode[] getChildren() {
//		if (this.children == null) {
//			getChildrenAsync(null);
//		}
		
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
//		if (children == null) {
//			getChildrenAsync(new ChildrenCallback() {
//				public void onSuccess() {
//					sortChildrenBy(comparator);
//				}
//
//				public void onFailure() { }
//			});
//		} 
		
		Arrays.sort(RemoteNode.this.children, comparator);
	}

	public interface ChildrenCallback {
		public abstract void onSuccess();
		public abstract void onFailure();
	}

	public void setChildren(RemoteNode[] children) {
		this.children = children;
	}
}
