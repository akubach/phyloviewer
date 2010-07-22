/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class IntersectTree {

	private ITree tree;
	private INode hit;
	private Vector2 position;
	private double distanceForHit=0.025;
	private ILayout layout;

	public IntersectTree(ITree tree, Vector2 position, ILayout layout) {
		this.tree = tree;
		this.position = position;
		this.layout = layout;
	}
	
	public void intersect() {
		if ( tree != null && tree.getRootNode() != null ) {
			this.visit(tree.getRootNode());
		}
	}
	
	public INode hit() {
		return hit;
	}
	
	private void visit(INode node) {
		if(node == null || this.position == null ) {
			return;
		}
		
		intersectNode(node);
		
		// If the position is contained in the boundingbox, continue the traversal.
		if( layout.getBoundingBox(node).contains(position)) {
			this.traverse(node);
		}
	}

	private void intersectNode(INode node) {
		Vector2 nodePosition = layout.getPosition(node);
		
		double distance = position.subtract ( nodePosition ).length();
		
		if ( distance < distanceForHit )  {
			
			// If we already have a hit, only set if the new one is closer.
		    if ( hit != null )
		    {
		      Vector2 hitPosition = layout.getPosition(hit);
		      if ( distance < position.subtract(hitPosition).length() )
		      {
		        hit = node;
		      }
		    }
		    else
		    {
		      hit = node;
		    }
		}
	}
	
	private void traverse(INode node) {
		for(int i = 0; i < node.getNumberOfChildren(); ++i) {
			this.visit(node.getChild(i));
		}
	}
}
