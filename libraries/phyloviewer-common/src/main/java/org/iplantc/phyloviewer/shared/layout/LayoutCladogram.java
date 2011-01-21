/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.layout;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

/**
 * This isn't really a cladogram layout anymore. It's sort of a "tree-space" layout that can be
 * transformed into layout information for other rendering types (ie Circular). I think this will allow
 * us to use one layout algorithm for multiple renderers. (at least if the layout is rooted, not sure
 * about unrooted.)
 * 
 * @author adamkubach
 * 
 */
public class LayoutCladogram implements ILayoutData
{
	private double xCanvasSize = 0.8; // Leave room for taxon labels.
	private double yCanvasSize = 1.0;
	int maximumLeafDepth = 0;
	Vector<Double> xPositions = null;
	double yLeafSpacing = 0;
	double currentY = 0;
	boolean useBranchLengths = false;
	double maximumDistanceToLeaf = 0.0;

	LayoutStorage storage = new LayoutStorage();

	public LayoutCladogram(double xCanvasSize, double yCanvasSize)
	{
		this.xCanvasSize = xCanvasSize;
		this.yCanvasSize = yCanvasSize;
	}

	public LayoutCladogram()
	{
	}

	public void layout(ITree tree)
	{
		if(tree == null)
		{
			return;
		}

		INode root = tree.getRootNode();
		if(root == null)
		{
			return;
		}

		// Allocate enough room for our nodes.
		int numberOfNodes = tree.getNumberOfNodes();
		init(numberOfNodes);

		// Figure out how much space we will need between leaf nodes.
		int numLeaves = root.getNumberOfLeafNodes();
		yLeafSpacing = yCanvasSize / numLeaves;
		currentY = yCanvasSize - (yLeafSpacing / 2.0);

		maximumLeafDepth = root.findMaximumDepthToLeaf();

		// Calculate the x positions.
		int numXPositions = maximumLeafDepth + 1;
		xPositions = new Vector<Double>(numXPositions);
		for(int i = 0;i < numXPositions;++i)
		{
			double ratio = (double)i / maximumLeafDepth;
			xPositions.add(i, ratio * xCanvasSize);
		}

		maximumDistanceToLeaf = root.findMaximumDistanceToLeaf();
		this.layoutNode(root, 0, 0.0);
	}

	public void init(int numberOfNodes)
	{
		storage.init(numberOfNodes);
	}

	private int layoutNode(INode node, int depth, double distanceFromRoot)
	{
		// Create empty bounding box and vector.
		Box2D bbox = new Box2D();
		Vector2 position = new Vector2();

		this.setBoundingBox(node, bbox);
		this.setPosition(node, position);

		int numChildren = node.getNumberOfChildren();
		int maxChildHeight = -1;

		if(0 == numChildren)
		{
			position.setY(currentY);
			currentY -= yLeafSpacing;
		}
		else
		{
			double sumChildrenY = 0.0;

			for(int childIndex = 0;childIndex < numChildren;++childIndex)
			{
				INode childNode = node.getChild(childIndex);

				// Layout the children.
				int height = this.layoutNode(childNode, depth + 1,
						distanceFromRoot + node.getBranchLength());
				maxChildHeight = Math.max(maxChildHeight, height);
				sumChildrenY += getPosition(childNode).getY();

				bbox.expandBy(getBoundingBox(childNode));
				bbox.expandBy(getPosition(childNode));
			}

			// Set our position.
			position.setY(sumChildrenY / numChildren);

		}

		int myHeight = maxChildHeight + 1;

		double xPosition = 0.0;
		if(useBranchLengths && maximumDistanceToLeaf != 0.0)
		{
			xPosition = ((distanceFromRoot + node.getBranchLength()) / maximumDistanceToLeaf)
					* xCanvasSize;
		}
		else
		{
			xPosition = xPositions.get(maximumLeafDepth - myHeight);
		}
		position.setX(xPosition);
		double halfYLeafSpacing = yLeafSpacing / 2.0;
		bbox.expandBy(new Vector2(Math.max(position.getX() - halfYLeafSpacing, 0.0), position.getY()
				- halfYLeafSpacing));
		bbox.expandBy(new Vector2(position.getX(), position.getY() + halfYLeafSpacing));

		return myHeight;
	}

	protected void setBoundingBox(INode node, Box2D box2d)
	{
		storage.setBoundingBox(node, box2d);
	}

	protected void setPosition(INode node, Vector2 vector2)
	{
		storage.setPosition(node, vector2);
	}

	@Override
	public Box2D getBoundingBox(INode node)
	{
		return this.getBoundingBox(node.getId());
	}

	@Override
	public Box2D getBoundingBox(int nodeId)
	{
		return storage.getBoundingBox(nodeId);
	}

	private Box2D getBoundingBox(Integer key)
	{
		return storage.getBoundingBox(key);
	}

	@Override
	public Vector2 getPosition(INode node)
	{
		return storage.getPosition(node);
	}

	public Vector2 getPosition(Integer key)
	{
		return storage.getPosition(key);
	}

	@Override
	public boolean containsNode(INode node)
	{
		return storage.containsNode(node);
	}

	/**
	 * Gets the keys present in the HashMap
	 * 
	 * @return Set of integers that corresponds to the node id's contained in the layout.
	 */
	private Set<Integer> keySet()
	{
		return storage.keySet();
	}

	public boolean isUseBranchLengths()
	{
		return useBranchLengths;
	}

	public void setUseBranchLengths(boolean useBranchLengths)
	{
		this.useBranchLengths = useBranchLengths;
	}

	public String toJSON()
	{
		String positions = "{";
		String bounds = "{";

		Set<Integer> keys = this.keySet();
		Iterator<Integer> iter = keys.iterator();
		while(iter.hasNext())
		{
			Integer key = iter.next();
			Vector2 position = this.getPosition(key);
			Box2D box = this.getBoundingBox(key);
			positions += "\"" + key.toString() + "\":" + position.toJSON();
			bounds += "\"" + key.toString() + "\":" + box.toJSON();

			if(iter.hasNext())
			{
				positions += ",";
				bounds += ",";
			}
		}

		positions += "}";
		bounds += "}";

		return "{\"bounds\":" + bounds + ",\"positions\":" + positions + "}";
	}
}
