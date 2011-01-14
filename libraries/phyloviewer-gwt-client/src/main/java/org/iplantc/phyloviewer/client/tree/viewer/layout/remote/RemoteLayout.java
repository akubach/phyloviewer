package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

public class RemoteLayout implements ILayoutData {

	private Map<Integer, Vector2> positions = new HashMap<Integer, Vector2>();
	private Map<Integer, Box2D> bounds = new HashMap<Integer, Box2D>();

	public RemoteLayout() {
	}
	
	@Override
	public Box2D getBoundingBox(INode node) {
		return bounds.get(node.getId());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return positions.get(node.getId());
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
	
	public void handleResponse(LayoutResponse response) {
		bounds.put(response.nodeID, response.boundingBox);
		positions.put(response.nodeID, response.position);
	}
}
