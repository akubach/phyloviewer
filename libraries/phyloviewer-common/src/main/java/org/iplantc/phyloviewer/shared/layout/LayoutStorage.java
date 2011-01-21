package org.iplantc.phyloviewer.shared.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

public class LayoutStorage implements ILayoutData
{
	private Map<Integer,Vector2> positions = new HashMap<Integer,Vector2>();
	private Map<Integer,Box2D> bounds = new HashMap<Integer,Box2D>();

	public LayoutStorage()
	{
	}

	@Override
	public Box2D getBoundingBox(INode node)
	{
		return this.getBoundingBox(node.getId());
	}

	@Override
	public Box2D getBoundingBox(int nodeId)
	{
		return bounds.get(nodeId);
	}

	public Box2D getBoundingBox(Integer key)
	{
		return bounds.get(key);
	}

	@Override
	public Vector2 getPosition(INode node)
	{
		return positions.get(node.getId());
	}
	
	public Vector2 getPosition(Integer key)
	{
		return positions.get(key);
	}

	public boolean containsNode(INode node)
	{
		return this.positions.containsKey(node.getId());
	}

	public boolean containsNodes(INode[] nodes)
	{
		for(int i = 0;i < nodes.length;i++)
		{
			if(!this.containsNode(nodes[i]))
			{
				return false;
			}
		}
		return true;
	}

	public void init(int numberOfNodes)
	{
		positions = new HashMap<Integer,Vector2>(numberOfNodes);
		bounds = new HashMap<Integer,Box2D>(numberOfNodes);
	}

	public void setPositionAndBounds(int nodeId, Vector2 position, Box2D box)
	{
		bounds.put(nodeId, box);
		positions.put(nodeId, position);
	}

	public void setBoundingBox(INode node, Box2D box2d)
	{
		bounds.put(node.getId(), box2d);
	}

	public void setPosition(INode node, Vector2 vector2)
	{
		positions.put(node.getId(), vector2);
	}

	public Set<Integer> keySet()
	{
		return positions.keySet();
	}
}
