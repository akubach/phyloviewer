package org.iplantc.phyloviewer.client.tree.viewer.layout;

import java.util.HashMap;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public class LayoutCladogramHashMap extends LayoutCladogram {
	
	HashMap<String, Box2D> bounds = new HashMap<String, Box2D>();
	HashMap<String, Vector2> positions = new HashMap<String, Vector2>();

	public LayoutCladogramHashMap(double xCanvasSize, double yCanvasSize) {
		super(xCanvasSize, yCanvasSize);
	}

	public LayoutCladogramHashMap() { }
	
	@Override
	protected void setBoundingBox(INode node, Box2D box2d) {
		this.bounds.put(node.getUUID(), box2d);
	}

	@Override
	protected void setPosition(INode node, Vector2 vector2) {
		this.positions.put(node.getUUID(), vector2);
	}

	@Override
	public Box2D getBoundingBox(INode node) {
		return this.bounds.get(node.getUUID());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return this.positions.get(node.getUUID());
	}
	
	@Override
	protected void setSize(int numberOfNodes) {
		this.positions = new HashMap<String, Vector2>(numberOfNodes);
		this.bounds = new HashMap<String, Box2D>(numberOfNodes);
	}

}
