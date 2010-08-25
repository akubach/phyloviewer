package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.HashMap;
import java.util.Map;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RemoteLayout implements ILayout {
	public static final RemoteLayoutServiceAsync service = (RemoteLayoutServiceAsync) GWT.create(RemoteLayoutService.class);
	
	private String layoutID;
	private final ILayout algorithm;
	
	private Map<String, Vector2> positions = new HashMap<String, Vector2>();
	private Map<String, Box2D> bounds = new HashMap<String, Box2D>();

	public RemoteLayout(ILayout algorithm) {
		this.algorithm = algorithm;
	}
	
	public ILayout getAlgorithm() {
		return algorithm;
	}
	
	@Override
	public Box2D getBoundingBox(INode node) {
		return bounds.get(node.getUUID());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return positions.get(node.getUUID());
	}
	
	public void getLayoutAsync(INode[] nodes, GotLayouts callback) {
		service.getLayout(nodes, layoutID, callback);
	}
	
	public boolean containsNode(INode node) {
		return this.bounds.containsKey(node.getUUID()) && this.positions.containsKey(node.getUUID());
	}
	
	public boolean containsNodes(INode[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			if (!this.containsNode(nodes[i])) {
				return false;
			}
		}
		return true;
	}

	public String getLayoutID() {
		return layoutID;
	}

	@Override
	public void layout(final ITree tree) {
		if (tree instanceof Tree) {
			this.layoutAsync((Tree) tree, null);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public void layoutAsync(final Tree tree, final DidLayout callback) {
		clear();
		
		service.layout(tree.getId(), this.getAlgorithm(), new DidLayout() {
			
			@Override
			protected void didLayout(final String layoutID) {
				service.getLayout(tree.getRootNode(), layoutID, new GotLayout() {
					
					@Override
					protected void gotLayout(LayoutResponse responses) {
						callback.didLayout(layoutID);
					}
				});
			}
		});
	}
	
	public void clear() {
		positions.clear();
		bounds.clear();
	}
	
	public abstract class GotLayout implements AsyncCallback<LayoutResponse> {

		protected abstract void gotLayout(LayoutResponse responses);

		@Override
		public final void onSuccess(LayoutResponse response) {
			handleResponse(response);
			gotLayout(response);
		}
		
		@Override
		public void onFailure(Throwable thrown) {
			// TODO Auto-generated method stub
			thrown.printStackTrace();
		}
		
		private void handleResponse(LayoutResponse response) {
			bounds.put(response.nodeID, response.boundingBox);
			positions.put(response.nodeID, response.position);
		}
	}
	
	public abstract class GotLayouts implements AsyncCallback<LayoutResponse[]> {
		
		protected abstract void gotLayouts(LayoutResponse[] responses);
		
		@Override
		public final void onSuccess(LayoutResponse[] responses) {
			handleResponses(responses);
			gotLayouts(responses);
		}
		
		@Override
		public void onFailure(Throwable thrown) {
			// TODO Auto-generated method stub
			thrown.printStackTrace();
		}
		
		private void handleResponse(LayoutResponse response) {
			bounds.put(response.nodeID, response.boundingBox);
			positions.put(response.nodeID, response.position);
		}
		
		private void handleResponses(LayoutResponse[] responses) {
			for (int i = 0; i < responses.length; i++) {
				handleResponse(responses[i]);
			}
		}
	}
	
	public abstract class DidLayout implements AsyncCallback<String> {
		
		protected abstract void didLayout(String layoutID);

		@Override
		public final void onSuccess(String layoutID) {
			RemoteLayout.this.layoutID = layoutID;
			didLayout(layoutID);
		}
		
		@Override
		public void onFailure(Throwable arg0) {
			// TODO Auto-generated method stub
		}
	}

}
