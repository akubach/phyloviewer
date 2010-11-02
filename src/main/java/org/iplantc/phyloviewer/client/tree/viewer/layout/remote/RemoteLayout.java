package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.layout.ILayoutCircular;
import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RemoteLayout implements ILayout, ILayoutCircular {
	public static CombinedServiceAsync service;
	
	private final ILayout.LayoutType algorithm;
	
	private Map<Integer, Vector2> positions = new HashMap<Integer, Vector2>();
	private Map<Integer, Box2D> bounds = new HashMap<Integer, Box2D>();
	private Map<Integer, PolarVector2> polarPositions = new HashMap<Integer, PolarVector2>();
	private Map<Integer, AnnularSector> polarBounds = new HashMap<Integer, AnnularSector>();

	public RemoteLayout(ILayout.LayoutType algorithm) {
		this.algorithm = algorithm;
	}
	
	public LayoutType getType() {
		return algorithm;
	}
	
	public String getLayoutID() {
		return this.getType().toString();
	}
	
	public static void setService(CombinedServiceAsync service) {
		RemoteLayout.service = service;
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
		if (this.containsNodes(nodes))
		{
			//already have the layouts requested, callback now
			LayoutResponse[] responses = new LayoutResponse[nodes.length];
			for (int i = 0; i < nodes.length; i++)
			{
				responses[i] = createResponse(nodes[i]);
			}
			
			callback.gotLayouts(responses);
		}
		else
		{
			service.getLayout(nodes, this.getLayoutID(), callback);
		}
	}
	
	public void getLayoutAsync(final INode node, final GotLayout callback) {
		if (this.containsNode(node))
		{
			//already have the layouts requested, callback now
			LayoutResponse response = createResponse(node);
			callback.gotLayout(response);
		}
		else
		{
			service.getLayout(node, this.getLayoutID(), callback);
		}
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

	@Override
	public void layout(final ITree tree) {
		throw new UnsupportedOperationException("RemoteLayout does not support layout(ITree).  Use layoutAsync(final Tree tree, final DidLayout callback).");
	}
	
	public void init(int numberOfNodes) {
		positions = new HashMap<Integer, Vector2>(numberOfNodes);
		bounds = new HashMap<Integer, Box2D>(numberOfNodes);
		polarPositions = new HashMap<Integer, PolarVector2>(numberOfNodes);
		polarBounds = new HashMap<Integer, AnnularSector>(numberOfNodes);
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
	
	private LayoutResponse createResponse(final INode node)
	{
		LayoutResponse response = new LayoutResponse();
		response.nodeID = node.getId();
		response.layoutID = this.getLayoutID();
		response.boundingBox = bounds.get(node);
		response.position = positions.get(node);
		response.polarBounds = polarBounds.get(node);
		response.polarPosition = polarPositions.get(node);
		return response;
	}
}
