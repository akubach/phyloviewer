package org.iplantc.phyloviewer.client.tree.viewer.layout.remote;

import java.util.HashMap;
import java.util.Map;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class RemoteLayout implements ILayout {
	private String layoutID;
	
	private Map<String, Vector2> positions = new HashMap<String, Vector2>();
	private Map<String, Box2D> bounds = new HashMap<String, Box2D>();	

	@Override
	public Box2D getBoundingBox(INode node) {
		return bounds.get(node.getUUID());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return positions.get(node.getUUID());
	}
	
	public void setBoundingBox(INode node, Box2D box) {
		bounds.put(node.getUUID(), box);
	}

	public void setPosition(INode node, Vector2 position) {
		positions.put(node.getUUID(), position);
	}

	public String getLayoutID() {
		return layoutID;
	}
	
	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
	}

	@Override
	public void layout(ITree tree) {
		throw new UnsupportedOperationException();
	}
	
	public void clear() {
		positions.clear();
		bounds.clear();
	}
}
