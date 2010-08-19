package org.iplantc.phyloviewer.client.tree.viewer.model.remote;

import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteNode implements INode, IsSerializable {
	private static RemoteNodeServiceAsync service = GWT.create(RemoteNodeService.class); //not sure what happens to this on the servlet...
	
	//assume these are all set by the servlet when the node is created
	private int id; //note to self, for now ID will stay, but it looks like id is only used to index layout Vectors, so if I do layout on the server, id isn't really needed anymore.  Would rather use UUIDs for servlet.
	private UUID uuid;
	private String label;
	private boolean isLeaf;
	private int numLeaves;
	private int height;
	
	private transient RemoteNode[] children; //will not be serialized
	
	/** required for serialization */
	public RemoteNode() { }
	
	
	/*
	 * Methods that do not trigger a fetch
	 */
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public Boolean isLeaf() {
		return isLeaf;
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
	
	@Override
	public int getNumberOfChildren() {
		if (children == null) {
			getChildrenAsync(null);
			TreeWidget.instance.requestRender(); //FIXME a hacky way of triggering a render
			return Integer.MAX_VALUE; //FIXME a hacky way of preventing the renderer from trying to get children.  anyone else that calls this is likely to *asplode*.
		}
		return children.length;
	}

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
	
	private void getChildrenAsync(final ChildrenCallback callback) {
		if (this.children == null) {

			//TODO find a way to batch these requests, in case several nodes want to get children during the same frame render
			service.getChildren(this.uuid, new AsyncCallback<RemoteNode[]>() {
				
				@Override
				public void onSuccess(RemoteNode[] children) {
					RemoteNode.this.children = children;
					callback.onSuccess();
				}

				@Override public void onFailure(Throwable arg0) { }
			});
			
		} else if (callback != null) {
			callback.onSuccess();
		}
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
		if (children == null) {
			getChildrenAsync(new ChildrenCallback() {
				public void onSuccess() {
					sortChildrenBy(comparator);
				}

				public void onFailure() { }
			});
		} 
		
		Arrays.sort(RemoteNode.this.children, comparator);
	}

	private interface ChildrenCallback {
		public abstract void onSuccess();
		public abstract void onFailure();
	}
}
