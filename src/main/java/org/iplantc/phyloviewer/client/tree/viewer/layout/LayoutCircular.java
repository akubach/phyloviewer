package org.iplantc.phyloviewer.client.tree.viewer.layout;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LayoutCircular implements ILayoutCircular, IsSerializable {

	private double layoutRadius;
	private Vector2 center;
	
//	private transient Vector<PolarVector2> positions = new Vector<PolarVector2>();
//	private transient Vector<AnnularSector> polarBounds = new Vector<AnnularSector>();
	private transient Map<Integer, PolarVector2> positions = new HashMap<Integer, PolarVector2>();
	private transient Map<Integer, AnnularSector> polarBounds = new HashMap<Integer, AnnularSector>();
	private transient double nextLeafAngle;
	private transient double angleStep;
	private transient double radiusStep;

	public LayoutCircular(double radius) {
		this.layoutRadius = radius;
		this.center = new Vector2(layoutRadius,layoutRadius);
	}
	
	/**
	 * no-arg constructor for serialization
	 */
	public LayoutCircular() {};
	
	public String getId() {
		return this.getClass().getName();
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

	private void init(int size) {
		positions = new HashMap<Integer, PolarVector2>(size);
		polarBounds = new HashMap<Integer, AnnularSector>(size);
	}

	@Override
	public boolean containsNode(INode node) {
		return positions.containsKey(node.getId());
	}
}
