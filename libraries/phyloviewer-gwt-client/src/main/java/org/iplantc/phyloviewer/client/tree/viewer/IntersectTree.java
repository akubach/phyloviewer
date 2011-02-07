/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.scene.Drawable;
import org.iplantc.phyloviewer.shared.scene.DrawableContainer;

public class IntersectTree
{
	public class Hit
	{
		private INode node;
		private Drawable drawable;

		Hit(INode node, Drawable drawable)
		{
			this.node = node;
			this.drawable = drawable;
		}

		public INode getNode()
		{
			return node;
		}

		public int getNodeId()
		{
			return node != null ? node.getId() : -1;
		}

		public Drawable getDrawable()
		{
			return drawable;
		}
	}

	private ITree tree;
	private Vector2 position;
	private double distanceForHitSquared;
	private IDocument document;
	private ILayoutData layout;
	private DrawableContainer drawableContainer;
	private ArrayList<Hit> hitList = new ArrayList<Hit>();

	public IntersectTree(IDocument document, DrawableContainer drawableContainer, Vector2 position,
			double pixelSize)
	{
		this.document = document;

		if(document != null)
		{
			this.tree = document.getTree();
			this.layout = document.getLayout();
		}

		this.drawableContainer = drawableContainer;
		this.position = position;
		distanceForHitSquared = pixelSize * pixelSize;
	}

	/**
	 * Get the hit object that was closest to the position.
	 * 
	 * @return
	 */
	public Hit getClosestHit()
	{
		Hit closest = null;
		double minDistance = Double.MAX_VALUE;
		for(Hit hit : hitList)
		{
			Box2D box = hit.drawable.getBoundingBox();
			Vector2 center = box.getCenter();
			double distance = center.distance(position);
			if(distance < minDistance)
			{
				minDistance = distance;
				closest = hit;
			}
		}

		return closest;
	}

	/**
	 * Perform the intersection test.
	 */
	public void intersect()
	{
		if(tree != null && tree.getRootNode() != null && layout != null && drawableContainer != null
				&& document != null)
		{
			this.visit(tree.getRootNode());
		}
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
		Drawable[] drawables = drawableContainer.getNodeDrawables(node, document, layout);
		this.testIntersection(node, drawables);
		
		Drawable drawable = drawableContainer.getTextDrawable(node, document, layout);
		this.testIntersection(node, drawable);
	}

	private void testIntersection(INode node, Drawable[] drawables)
	{
		for(Drawable drawable : drawables)
		{
			testIntersection(node, drawable);
		}
	}

	private void testIntersection(INode node, Drawable drawable)
	{
		if(drawable.intersect(position, distanceForHitSquared))
		{
			Hit hit = new Hit(node, drawable);
			hitList.add(hit);
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

				Drawable[] drawables = drawableContainer.getBranchDrawables(node, child, document, layout);
				this.testIntersection(child, drawables);

				this.visit(child);
			}
		}
	}
}
