package org.iplantc.phyloviewer.shared.layout;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

public class LayoutCircular implements ILayoutCircular {

	private double layoutRadius;
	private Vector2 center;
	
//	private transient Vector<PolarVector2> positions = new Vector<PolarVector2>();
//	private transient Vector<AnnularSector> polarBounds = new Vector<AnnularSector>();
	private Map<Integer, PolarVector2> positions = new HashMap<Integer, PolarVector2>();
	private Map<Integer, AnnularSector> polarBounds = new HashMap<Integer, AnnularSector>();
	private double nextLeafAngle;
	private double angleStep;
	private double radiusStep;

	public LayoutCircular(double radius) {
		this.layoutRadius = radius;
		this.center = new Vector2(layoutRadius,layoutRadius);
	}
	
	/**
	 * no-arg constructor for serialization
	 */
	public LayoutCircular() {};
	
	public LayoutType getType() {
		return LayoutType.LAYOUT_TYPE_CIRCULAR;
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
		
		angleStep = 2 * Math.PI / root.getNumberOfLeafNodes();
		radiusStep = layoutRadius / root.findMaximumDepthToLeaf();
		nextLeafAngle = 0;
		
		layout(root);
	}
	
	private int layout(INode node) {
		PolarVector2 position;
		AnnularSector bounds = new AnnularSector();

		int myHeight = 0;
		
		if (node.isLeaf()) {
			position = new PolarVector2(this.layoutRadius, nextLeafAngle);
			nextLeafAngle += angleStep;
		} else {
			double childTotalAngle = 0;
			int maxChildHeight = -1;

			for (int i = 0; i < node.getNumberOfChildren(); i++) {
				INode child = node.getChild(i);
				
				int childHeight = layout(child);
				
				bounds.expandBy(polarBounds.get(child.getId()));
				childTotalAngle += getPolarPosition(child).getAngle();
				maxChildHeight = Math.max(maxChildHeight, childHeight);
			}

			myHeight = maxChildHeight + 1;
			double radius = this.layoutRadius - myHeight * radiusStep;
			position = new PolarVector2(radius, childTotalAngle / node.getNumberOfChildren());
		}
		
		bounds.expandBy(position);
		positions.put(node.getId(), position);
		polarBounds.put(node.getId(), bounds);
		
		return myHeight;
	}

	public void init(int numberOfNodes) {
		positions = new HashMap<Integer, PolarVector2>(numberOfNodes);
		polarBounds = new HashMap<Integer, AnnularSector>(numberOfNodes);
	}

	@Override
	public boolean containsNode(INode node) {
		return positions.containsKey(node.getId());
	}
}
