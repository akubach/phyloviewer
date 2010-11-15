package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import java.util.HashMap;

import org.iplantc.phyloviewer.shared.model.INode;

public class StyleById implements IStyleMap
{
	HashMap<Integer, INodeStyle> map = new HashMap<Integer, INodeStyle>();
	
	@Override
	public INodeStyle get(INode node)
	{
		return map.get(node.getId());
	}

	@Override
	public void put(INode node, INodeStyle style)
	{
		map.put(node.getId(), style);
	}

	@Override
	public void clear()
	{
		map.clear();
	}

}
