package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.HashSet;
import java.util.Set;

import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

public class IntersectTreeBox 
{
	public static Set<INode> intersect(INode node, ILayout layout, Box2D range)
	{
		Set<INode> result = new HashSet<INode>();
		boolean ready = node != null && layout != null && range != null && range.valid();
		
		if (ready) 
		{
			visit(node, layout, range, result);
		}
		
		return result;
	}
	
	static void visit(INode node, ILayout layout, Box2D range, Set<INode> result) 
	{
		Box2D boundingBox = layout.getBoundingBox(node);
		Vector2 position = layout.getPosition(node);
		if (node != null && boundingBox != null && position != null) 
		{
			boolean nodeInRange = range.contains(position);
			if (nodeInRange) 
			{
				result.add(node);
			}
			
			if (nodeInRange || boundingBox.intersects(range)) 
			{
				traverse(node, layout, range, result);
			}
		}
	}

	static void traverse(INode node, ILayout layout, Box2D range, Set<INode> result) {
		INode[] children = node.getChildren();
		if (children != null)
		{
			for(int i = 0; i < children.length; ++i) {
				visit(children[i], layout, range, result);
			}
		}
	}
}
