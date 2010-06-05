package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.HashMap;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class LayoutCircular implements ILayoutCircular {

	private double layoutRadius;
	private HashMap<INode, PolarVector2> positions = new HashMap<INode, PolarVector2>();
	private HashMap<INode, AnnularSector> polarBounds = new HashMap<INode, AnnularSector>();
	
	public LayoutCircular(double radius) {
		this.layoutRadius = radius;
	}
	
	@Override
	public PolarVector2 getPosition(INode node) {
		return positions.get(node);
	}
	
	@Override
	public Box2D getBoundingBox(INode node) {
		return polarBounds.get(node).cartesianBounds();
	}

	public AnnularSector getPolarBoundingBox(INode node) {
		return polarBounds.get(node);
	}
	
	public void layout(ITree tree) {	
		INode root = tree.getRootNode();
		if ( root == null ) {
			return;
		}

		clear();
		
		double angleStep = 2 * Math.PI / root.getNumberOfLeafNodes();
		double radiusStep = layoutRadius / root.findMaximumDepthToLeaf();
		double nextRadius = 0;
		double nextLeafAngle = 0;
		
		layout(root, nextRadius, nextLeafAngle, angleStep, radiusStep);
	}
	
	private double layout(INode node, double radius, double nextLeafAngle, double angleStep, double radiusStep) {
		PolarVector2 position;
		AnnularSector bounds = new AnnularSector();

		if (node.isLeaf()) {
			position = new PolarVector2(this.layoutRadius, nextLeafAngle);
			nextLeafAngle += angleStep;
		} else {
			double childTotalAngle = 0;
			//double minChildRadius = Double.MAX_VALUE;
			for (int i = 0; i < node.getNumberOfChildren(); i++) {
				INode child = node.getChild(i);
				
				nextLeafAngle = layout(child, radius + radiusStep, nextLeafAngle, angleStep, radiusStep);
				
				bounds.expandBy(polarBounds.get(child));
				childTotalAngle += getPosition(child).getAngle();
				//minChildRadius = Math.min(minChildRadius, getPosition(child).getRadius());
			}

			position = new PolarVector2(radius, childTotalAngle / node.getNumberOfChildren());
		}
		
		bounds.expandBy(position);
		System.out.println("node at " + position + " with bounds " + bounds);
		positions.put(node, position);
		polarBounds.put(node, bounds);
		
		return nextLeafAngle;
	}

	private void clear() {
		positions.clear();
		polarBounds.clear();
	}
}
