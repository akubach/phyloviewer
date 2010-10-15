package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayoutCircular;
import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RemoteLayout implements ILayout, ILayoutCircular {
	public static CombinedServiceAsync service;
	
	private String layoutID;
	private final ILayout algorithm;
	
	private Map<Integer, Vector2> positions = new HashMap<Integer, Vector2>();
	private Map<Integer, Box2D> bounds = new HashMap<Integer, Box2D>();
	private Map<Integer, PolarVector2> polarPositions = new HashMap<Integer, PolarVector2>();
	private Map<Integer, AnnularSector> polarBounds = new HashMap<Integer, AnnularSector>();

	public RemoteLayout(ILayout algorithm) {
		this.algorithm = algorithm;
		this.layoutID = algorithm.getId();
	}
	
	public String getId() {
		return layoutID;
	}
	
	public static void setService(CombinedServiceAsync service) {
		RemoteLayout.service = service;
	}
	
	public ILayout getAlgorithm() {
		return algorithm;
	}
	
	@Override
	public Box2D getBoundingBox(INode node) {
		return bounds.get(node.getId());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return positions.get(node.getId());
	}
	
	@Override
	public AnnularSector getPolarBoundingBox(INode node) {
		return polarBounds.get(node.getId());
	}

	@Override
	public PolarVector2 getPolarPosition(INode node) {
		return polarPositions.get(node.getId());
	}
	
	public void getLayoutAsync(final INode[] nodes, final GotLayouts callback) {
		service.getLayout(nodes, layoutID, callback);
	}
	
	public void getLayoutAsync(final INode node, final GotLayout callback) {
		service.getLayout(node, layoutID, callback);
	}
	
	public boolean containsNode(INode node) {
		return this.positions.containsKey(node.getId());
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
	
	public void layoutAsync(final ITree tree, DidLayout callback) {
		if (tree == null) {
			return; 
		}
	}
	
	public void clear() {
		positions.clear();
		bounds.clear();
		polarPositions.clear();
		polarBounds.clear();
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
			RemoteLayout.this.layoutID = layoutID;
			didLayout(layoutID);
		}

		@Override
		public void onFailure(Throwable thrown) {
			RemoteLayout.this.layoutID = null;
			GWT.log("DidLayout received an exception from the remote service.", thrown);
		}
	}
}
