package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class LayoutCircular {
	private double cladogramWidth;
	private double cladogramHeight;
	private LayoutCladogram cladogram;
	
	public LayoutCircular(double xCanvasSize, double yCanvasSize) {
		double diameter = Math.min(xCanvasSize, yCanvasSize);
		cladogramWidth = diameter / 2;
		cladogramHeight = Math.PI * diameter;
		cladogram = new LayoutCladogram(cladogramWidth, cladogramHeight);
	}
	
	public void layout(ITree tree) {
		cladogram.layout(tree);
		transform(tree);
	}
	
	private void transform(ITree tree) {
		transform(tree.getRootNode());
	}
	
	private void transform(INode node) {
		Vector2 pos = node.getPosition();
		double r = pos.getX();
		double a = 2 * Math.PI * (pos.getY() / cladogramHeight);
		
		pos.setX(r * Math.cos(a) + cladogramWidth);
		pos.setY(r * Math.sin(a));
		
		for (int i = 0; i < node.getNumberOfChildren(); i++) {
			transform(node.getChild(i));
		}
	}

}
