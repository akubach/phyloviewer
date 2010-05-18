package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.Vector;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;


public class LayoutCladogram {

	private double _xCanvasSize = 0.8; // Leave room for taxon labels.
	private double _yCanvasSize = 1.0;
	int _maximumLeafDepth = 0;
	Vector<Double> _xPositions = null;
	double _yLeafSpacing = 0;
	double _currentY = 0;
	
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
		
		node.setBoundingBox(new Box2D());
		node.setPosition(new Vector2());

		int myDepth = _maximumLeafDepth - node.findMaximumDepthToLeaf();
    	double xPosition = _xPositions.get(myDepth);

  		node.getPosition().setX(xPosition);

  		int numChildren = node.getNumberOfChildren();
  		if ( 0 == numChildren ) {
			node.getPosition().setY (_currentY);
			_currentY -= _yLeafSpacing;
			
	    	node.getBoundingBox().expandBy ( node.getPosition() );
  		}
  		else  {
    		double sumChildrenY = 0.0;

		    for ( int child = 0; child < numChildren; ++child )
		    {
		    	// Layout the children.
		    	this._layoutNode ( node.getChild(child), depth + 1);
		    	sumChildrenY += node.getChild(child).getPosition().getY();
			  
		    	node.getBoundingBox().expandBy ( node.getChild(child).getBoundingBox() );
		    	node.getBoundingBox().expandBy ( node.getChild(child).getPosition() );
		    }

	   	 	// Set our position.
	    	node.getPosition().setY ( sumChildrenY / numChildren );
	    	node.getBoundingBox().expandBy ( node.getPosition() );
	  	}
	}
}
