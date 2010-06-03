package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.HashMap;
import java.util.Vector;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;


public class LayoutCladogram implements ILayout {

	private double _xCanvasSize = 0.8; // Leave room for taxon labels.
	private double _yCanvasSize = 1.0;
	int _maximumLeafDepth = 0;
	Vector<Double> _xPositions = null;
	double _yLeafSpacing = 0;
	double _currentY = 0;
	
	private HashMap<INode, Vector2> positions = new HashMap<INode, Vector2>();
	private HashMap<INode, Box2D> bounds = new HashMap<INode, Box2D>();
	
	public LayoutCladogram(double xCanvasSize, double yCanvasSize) {
		_xCanvasSize=xCanvasSize;
		_yCanvasSize=yCanvasSize;
	}
	
	public void layout(ITree tree) {
		INode root = tree.getRootNode();
		if ( root == null ) {
			return;
		}
		
		// Figure out how much space we will need between leaf nodes.
	  	int numLeaves = root.getNumberOfLeafNodes();
	  	_yLeafSpacing = _yCanvasSize / numLeaves;
		_currentY = _yCanvasSize;

		_maximumLeafDepth = root.findMaximumDepthToLeaf();

		// Calculate the x positions.
		int numXPositions = _maximumLeafDepth + 1;
		_xPositions = new Vector<Double> ( numXPositions );
		for ( int i = 0; i < numXPositions; ++i ) {
			double ratio = (double)i / _maximumLeafDepth;
			_xPositions.add(i, ratio * _xCanvasSize);
		}
		
		this._layoutNode(root,0);
	}
	
	private void _layoutNode(INode node, int depth) {
		
		setBoundingBox(node, new Box2D());
		setPosition(node, new Vector2());

		int myDepth = _maximumLeafDepth - node.findMaximumDepthToLeaf();
    	double xPosition = _xPositions.get(myDepth);

  		getPosition(node).setX(xPosition);

  		int numChildren = node.getNumberOfChildren();
  		if ( 0 == numChildren ) {
			getPosition(node).setY (_currentY);
			_currentY -= _yLeafSpacing;
			
	    	getBoundingBox(node).expandBy ( getPosition(node) );
  		}
  		else  {
    		double sumChildrenY = 0.0;

		    for ( int child = 0; child < numChildren; ++child )
		    {
		    	// Layout the children.
		    	this._layoutNode ( node.getChild(child), depth + 1);
		    	sumChildrenY += getPosition(node.getChild(child)).getY();
			  
		    	getBoundingBox(node).expandBy ( getBoundingBox(node.getChild(child)) );
		    	getBoundingBox(node).expandBy ( getPosition(node.getChild(child)) );
		    }

	   	 	// Set our position.
	    	getPosition(node).setY ( sumChildrenY / numChildren );
	    	getBoundingBox(node).expandBy ( getPosition(node) );
	  	}
	}

	private void setBoundingBox(INode node, Box2D box2d) {
		this.bounds.put(node, box2d);
	}

	private void setPosition(INode node, Vector2 vector2) {
		this.positions.put(node, vector2);
	}

	@Override
	public Box2D getBoundingBox(INode node) {
		return this.bounds.get(node);
	}

	@Override
	public Vector2 getPosition(INode node) {
		return this.positions.get(node);
	}
}
