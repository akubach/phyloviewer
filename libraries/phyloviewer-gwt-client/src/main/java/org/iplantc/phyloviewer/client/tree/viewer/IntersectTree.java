/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

public class IntersectTree
{

	private ITree tree;
	private INode hit;
	private Vector2 position;
	private double distanceForHit;
	double distanceForHitSquared;
	private ILayoutData layout;

	public IntersectTree(ITree tree, Vector2 position, ILayoutData layout,double distanceForHit)
	{
		this.tree = tree;
		this.position = position;
		this.layout = layout;
		this.distanceForHit = distanceForHit;
		distanceForHitSquared = distanceForHit * distanceForHit;
	}

	public void intersect()
	{
		if(tree != null && tree.getRootNode() != null && layout != null)
		{
			this.visit(tree.getRootNode());
		}
	}

	public INode hit()
	{
		return hit;
	}

	private void visit(INode node)
	{
		if(node == null || this.position == null)
		{
			return;
		}

		if(layout.containsNode(node))
		{
			intersectNode(node);

			// If the position is contained in the boundingbox, continue the traversal.
			if(layout.getBoundingBox(node).contains(position))
			{
				this.traverse(node);
			}
		}
	}

	private void intersectNode(INode node)
	{
		Vector2 nodePosition = layout.getPosition(node);

		double distance = position.distanceSquared(nodePosition);

		if(distance < distanceForHitSquared)
		{
			// If we already have a hit, only set if the new one is closer.
			if(hit != null)
			{
				Vector2 hitPosition = layout.getPosition(hit);
				if(distance < position.distanceSquared(hitPosition))
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

	private void traverse(INode node)
	{
		INode[] children = node.getChildren();
		if(children != null)
		{
			for(int i = 0;i < children.length;++i)
			{
				this.visit(children[i]);
			}
		}
	}
}
