package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.Vector;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class LayoutCircular implements ILayoutCircular {

	private final double layoutRadius;
	private final Vector2 center;
	private final Vector<PolarVector2> positions = new Vector<PolarVector2>();
	private final Vector<AnnularSector> polarBounds = new Vector<AnnularSector>();

	public LayoutCircular(double radius) {
		this.layoutRadius = radius;
		this.center = new Vector2(layoutRadius,layoutRadius);
	}
	
	@Override
	public Vector2 getPosition(INode node) {
		return this.getPolarPosition(node).toCartesian(center);
	}
	
	@Override
	public PolarVector2 getPolarPosition(INode node) {
		return positions.get(node.getId());
	}
	
	@Override
	public Box2D getBoundingBox(INode node) {
		return this.getPolarBoundingBox(node).cartesianBounds(center);
	}

	public AnnularSector getPolarBoundingBox(INode node) {
		return polarBounds.get(node.getId());
	}
	
	public void layout(ITree tree) {	
		INode root = tree.getRootNode();
		if ( root == null ) {
			return;
		}

		init(tree.getNumberOfNodes());
		
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

			for (int i = 0; i < node.getNumberOfChildren(); i++) {
				INode child = node.getChild(i);
				
				nextLeafAngle = layout(child, radius + radiusStep, nextLeafAngle, angleStep, radiusStep);
				
				bounds.expandBy(polarBounds.get(child.getId()));
				childTotalAngle += getPolarPosition(child).getAngle();
			}

			position = new PolarVector2(radius, childTotalAngle / node.getNumberOfChildren());
		}
		
		bounds.expandBy(position);
		positions.set(node.getId(), position);
		polarBounds.set(node.getId(), bounds);
		
		return nextLeafAngle;
	}

	private void init(int size) {
		positions.setSize(size);
		polarBounds.setSize(size);
	}
}
