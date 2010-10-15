package org.iplantc.phyloviewer.client.tree.viewer.layout;

import java.util.HashMap;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public class LayoutCladogramHashMap extends LayoutCladogram {
	
	HashMap<Integer, Box2D> bounds = new HashMap<Integer, Box2D>();
	HashMap<Integer, Vector2> positions = new HashMap<Integer, Vector2>();

	public LayoutCladogramHashMap(double xCanvasSize, double yCanvasSize) {
		super(xCanvasSize, yCanvasSize);
	}

	public LayoutCladogramHashMap() { }
	
	@Override
	protected void setBoundingBox(INode node, Box2D box2d) {
		this.bounds.put(node.getId(), box2d);
	}

	@Override
	protected void setPosition(INode node, Vector2 vector2) {
		this.positions.put(node.getId(), vector2);
	}

	@Override
	public Box2D getBoundingBox(INode node) {
		return this.bounds.get(node.getId());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return this.positions.get(node.getId());
	}
	
	@Override
	protected void setSize(int numberOfNodes) {
		this.positions = new HashMap<Integer, Vector2>(numberOfNodes);
		this.bounds = new HashMap<Integer, Box2D>(numberOfNodes);
	}
	
	@Override
	public boolean containsNode(INode node) {
		return bounds.containsKey(node.getId());
	}
}
