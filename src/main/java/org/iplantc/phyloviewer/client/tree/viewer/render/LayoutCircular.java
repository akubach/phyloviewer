package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.HashMap;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class LayoutCircular implements ILayoutCircular {

	private double radius;
	private HashMap<INode, PolarVector2> positions = new HashMap<INode, PolarVector2>();
	private HashMap<INode, AnnularSector> polarBounds = new HashMap<INode, AnnularSector>();
	private HashMap<INode, INode> parents = new HashMap<INode, INode>();
	
	public LayoutCircular(double radius) {
		this.radius = radius;
	}
	
	@Override
	public PolarVector2 getPosition(INode node) {
		return positions.get(node);
	}
	
	@Override
	public Box2D getBoundingBox(INode node) {
		// TODO calculate cartesian bounds of polar bounding box
		throw new RuntimeException("Not yet implemented");
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
		double radiusStep = radius / root.findMaximumDepthToLeaf();
		double nextRadius = 0;
		double nextLeafAngle = 0;
		
		layout(root, nextRadius, nextLeafAngle, angleStep, radiusStep);
	}
	
	private double layout(INode node, double radius, double nextLeafAngle, double angleStep, double radiusStep) {
		PolarVector2 position = new PolarVector2(radius, nextLeafAngle);
		AnnularSector bounds = new AnnularSector(position);
		positions.put(node, position);
		polarBounds.put(node, bounds);
		
		if (node.isLeaf()) {
			updateAncestorPositions(node);
			nextLeafAngle += angleStep;
		} 

		for (int i = 0; i < node.getNumberOfChildren(); i++) {
			INode child = node.getChild(i);
			parents.put(child, node);
			nextLeafAngle = layout(child, radius + radiusStep, nextLeafAngle, angleStep, radiusStep);
			bounds.expandBy(polarBounds.get(child));
		}
		
		return nextLeafAngle;
	}

	private void clear() {
		positions.clear();
		polarBounds.clear();
		parents.clear();
	}
	
	private void updateAncestorBounds(INode child, Vector2 descendantPosition) {
		INode parent = parents.get(child);
		
		if (parent != null) {
			polarBounds.get(parent).expandBy(descendantPosition);
			updateAncestorBounds(parent, descendantPosition);
		}
	}
	
	private void updateAncestorPositions(INode child) {
		INode parent = parents.get(child);
		
		if (parent != null) {
			//position the parent at angular midpoint of children
			double childTotalAngle = 0;
			for (int i = 0; i < parent.getNumberOfChildren(); i++) {
				childTotalAngle += getPosition(parent.getChild(i)).getAngle();
			}
			positions.get(parent).setAngle(childTotalAngle / parent.getNumberOfChildren());
			
			updateAncestorPositions(parent);
		}
	}
}
