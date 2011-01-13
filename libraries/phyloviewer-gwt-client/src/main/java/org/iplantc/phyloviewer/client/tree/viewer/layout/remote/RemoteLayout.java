package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class RemoteLayout implements ILayoutData {
	public static CombinedServiceAsync service;
	
	private Map<Integer, Vector2> positions = new HashMap<Integer, Vector2>();
	private Map<Integer, Box2D> bounds = new HashMap<Integer, Box2D>();

	public RemoteLayout() {
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
			service.getLayout(nodes, callback);
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
			service.getLayout(node, callback);
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

	public void init(int numberOfNodes) {
		positions = new HashMap<Integer, Vector2>(numberOfNodes);
		bounds = new HashMap<Integer, Box2D>(numberOfNodes);
	}
	
	private void handleResponse(LayoutResponse response) {
		bounds.put(response.nodeID, response.boundingBox);
		positions.put(response.nodeID, response.position);
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
			Logger.getLogger("").log(Level.SEVERE, "GotLayout received an exception from the remote service.", thrown);
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
			Logger.getLogger("").log(Level.SEVERE, "GotLayouts received an exception from the remote service.", thrown);
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
		response.boundingBox = bounds.get(node);
		response.position = positions.get(node);
		return response;
	}
}
