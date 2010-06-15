package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.Vector;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;


public class LayoutCladogram implements ILayout {

	private double xCanvasSize = 0.8; // Leave room for taxon labels.
	private double yCanvasSize = 1.0;
	int maximumLeafDepth = 0;
	Vector<Double> xPositions = null;
	double yLeafSpacing = 0;
	double currentY = 0;
	
	private Vector<Vector2> positions = new Vector<Vector2>();
	private Vector<Box2D> bounds = new Vector<Box2D>();
	
	public LayoutCladogram(double xCanvasSize, double yCanvasSize) {
		this.xCanvasSize=xCanvasSize;
		this.yCanvasSize=yCanvasSize;
	}
	
	public void layout(ITree tree) {
		if ( tree == null ) {
			return;
		}
		
		INode root = tree.getRootNode();
		if ( root == null ) {
			return;
		}
		
		// Allocate enough room for our nodes.
		int numberOfNodes = tree.getNumberOfNodes();
		this.positions.setSize(numberOfNodes);
		this.bounds.setSize(numberOfNodes);
		
		// Figure out how much space we will need between leaf nodes.
	  	int numLeaves = root.getNumberOfLeafNodes();
	  	yLeafSpacing = yCanvasSize / numLeaves;
		currentY = yCanvasSize;

		maximumLeafDepth = root.findMaximumDepthToLeaf();

		// Calculate the x positions.
		int numXPositions = maximumLeafDepth + 1;
		xPositions = new Vector<Double> ( numXPositions );
		for ( int i = 0; i < numXPositions; ++i ) {
			double ratio = (double)i / maximumLeafDepth;
			xPositions.add(i, ratio * xCanvasSize);
		}
		
		this._layoutNode(root,0);
	}
	
	private void _layoutNode(INode node, int depth) {
		node.setNodeColor(Defaults.POINT_COLOR);
		
		// Create empty bounding box and vector.
		Box2D bbox = new Box2D();
		Vector2 position = new Vector2();
		
		this.setBoundingBox(node, bbox);
		this.setPosition(node, position);

		int myDepth = maximumLeafDepth - node.findMaximumDepthToLeaf();
    	double xPosition = xPositions.get(myDepth);

    	position.setX(xPosition);

  		int numChildren = node.getNumberOfChildren();
  		if ( 0 == numChildren ) {
			
  			position.setY (currentY);
			currentY -= yLeafSpacing;
			
			bbox.expandBy ( position );
  		}
  		else  {
    		double sumChildrenY = 0.0;

		    for ( int childIndex = 0; childIndex < numChildren; ++childIndex )
		    {
		    	INode childNode = node.getChild(childIndex);
		    	
		    	// Layout the children.
		    	this._layoutNode ( childNode, depth + 1);
		    	sumChildrenY += getPosition(childNode).getY();
			  
		    	bbox.expandBy ( getBoundingBox(childNode) );
		    	bbox.expandBy ( getPosition(childNode) );
		    }

	   	 	// Set our position.
		    position.setY ( sumChildrenY / numChildren );
	    	bbox.expandBy ( position );
	  	}
	}

	private void setBoundingBox(INode node, Box2D box2d) {
		this.bounds.set(node.getId(), box2d);
	}

	private void setPosition(INode node, Vector2 vector2) {
		this.positions.set(node.getId(), vector2);
	}

	@Override
	public Box2D getBoundingBox(INode node) {
		return this.bounds.get(node.getId());
	}

	@Override
	public Vector2 getPosition(INode node) {
		return this.positions.get(node.getId());
	}
}
