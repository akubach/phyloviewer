/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.scene.BranchBuilderCladogram;
import org.iplantc.phyloviewer.shared.scene.Drawable;

public class IntersectTree
{
	private ITree tree;
	private INode hit;
	private Vector2 position;
	double distanceForHitSquared;
	private ILayoutData layout;
	double pixelSize;
	
	// TODO: Need to pass in the branch builder for the layout type.
	BranchBuilderCladogram branchBuilder = new BranchBuilderCladogram();
	BranchHit branchHit;
	
	class BranchHit
	{
		int childId;
	}

	public IntersectTree(ITree tree, Vector2 position, ILayoutData layout, double pixelSize,
			int clickableBuffer)
	{
		this.tree = tree;
		this.position = position;
		this.layout = layout;
		this.pixelSize = pixelSize;
		double distanceForHit = pixelSize * clickableBuffer;
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
	
	public BranchHit getBranchHit()
	{
		return branchHit;
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
			Box2D bbox = layout.getBoundingBox(node).clone();
			bbox.expandBy(0.2);
			if(bbox.contains(position))
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
				INode child = children[i];

				Drawable[] drawables = branchBuilder.buildBranch(node, child, layout);
				for(Drawable drawable : drawables)
				{
					if(drawable.intersect(position, pixelSize * pixelSize))
					{
						branchHit = new BranchHit();
						branchHit.childId = child.getId();
					}
				}

				this.visit(child);
			}
		}
	}
}
