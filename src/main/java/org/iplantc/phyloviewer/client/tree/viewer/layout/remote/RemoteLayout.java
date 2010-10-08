package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayoutCircular;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RemoteLayout implements ILayout, ILayoutCircular {
	public static final RemoteLayoutServiceAsync service = (RemoteLayoutServiceAsync) GWT.create(RemoteLayoutService.class);
	
	private String layoutID;
	private final ILayout algorithm;
	
	private Map<String, Vector2> positions = new HashMap<String, Vector2>();
	private Map<String, Box2D> bounds = new HashMap<String, Box2D>();
	private Map<String, PolarVector2> polarPositions = new HashMap<String, PolarVector2>();
	private Map<String, AnnularSector> polarBounds = new HashMap<String, AnnularSector>();
	
	private ITree currentTree;
	
	private boolean doingLayout = false;
	private ArrayList<DidLayout> callbacks = new ArrayList<DidLayout>();

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
	
	@Override
	public AnnularSector getPolarBoundingBox(INode node) {
		return polarBounds.get(node.getUUID());
	}

	@Override
	public PolarVector2 getPolarPosition(INode node) {
		return polarPositions.get(node.getUUID());
	}
	
	public void getLayoutAsync(final INode[] nodes, final GotLayouts callback) {
		if (layoutID != null) {
			
			service.getLayout(nodes, layoutID, callback);
		
		} else if (doingLayout) {
			
			//layout not ready yet, add to waiting list and call self back
			callbacks.add(new DidLayout() {
				protected void didLayout(String layoutID) {
					getLayoutAsync(nodes, callback);
				}
			});

		} else {
			
			throw new RuntimeException("The layout hasn't been run.  Call layoutAsync() before trying to get node layouts.");
			
		}
		
	}
	
	public void getLayoutAsync(final INode node, final GotLayout callback) {
		if (layoutID != null) {
			
			service.getLayout(node, layoutID, callback);
			
		} else if (doingLayout) {
			
			//layout not ready yet, add to waiting list and call self back
			callbacks.add(new DidLayout() {
				protected void didLayout(String layoutID) {
					getLayoutAsync(node, callback);
				}
			});

		} else {
			
			throw new RuntimeException("The layout hasn't been run.  Call layoutAsync() before trying to get node layouts.");
			
		}
	}
	
	public boolean containsNode(INode node) {
		return this.positions.containsKey(node.getUUID());
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
		throw new UnsupportedOperationException("RemoteLayout does not support layout(ITree).  Use layoutAsync(final Tree tree, final DidLayout callback).");
	}
	
	public void layoutAsync(final Tree tree, final DidLayout callback) {
		if (tree == null) {
			return; 
		}
		
		if (tree.equals(currentTree)) {
			if (doingLayout) {
				callbacks.add(callback);
			} else {
				callback.didLayout(layoutID);
			}
		} else {	
			this.currentTree = tree;
			doingLayout = true;

			clear();
			callbacks.add(callback);
			
			service.layout(tree.getId(), this.getAlgorithm(), new DidLayout() {
				
				@Override
				protected void didLayout(final String layoutID) {
					RemoteLayout.this.currentTree = tree;
					
					service.getLayout(tree.getRootNode(), layoutID, new GotLayout() {
						
						@Override
						protected void gotLayout(LayoutResponse responses) {
							for (DidLayout callback : callbacks) {
								callback.didLayout(layoutID);
							}
							callbacks.clear();
						}
					});
				}
			});
		}
	}
	
	public void clear() {
		positions.clear();
		bounds.clear();
		polarPositions.clear();
		polarBounds.clear();
		callbacks.clear();
	}
	
	private void handleResponse(LayoutResponse response) {
		bounds.put(response.nodeID, response.boundingBox);
		positions.put(response.nodeID, response.position);
		
		if (response.polarBounds != null) {
			polarBounds.put(response.nodeID, response.polarBounds);
		}
		
		if (response.polarPosition != null) {
			polarPositions.put(response.nodeID, response.polarPosition);
		}
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
			GWT.log("GotLayout received an exception from the remote service.", thrown);
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
			GWT.log("GotLayouts received an exception from the remote service.", thrown);
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
			doingLayout = false;
			RemoteLayout.this.layoutID = layoutID;
			didLayout(layoutID);
		}
		
		@Override
		public void onFailure(Throwable thrown) {
			doingLayout = false;
			RemoteLayout.this.layoutID = null;
			RemoteLayout.this.currentTree = null;
			GWT.log("DidLayout received an exception from the remote service.", thrown);
		}
	}
}
