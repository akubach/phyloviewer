package org.iplantc.phyloviewer.shared.render.style;

import java.util.HashMap;

import org.iplantc.phyloviewer.shared.model.INode;

public class StyleById implements IStyleMap
{
	HashMap<Integer, IStyle> map = new HashMap<Integer, IStyle>();
	
	@Override
	public IStyle get(INode node)
	{
		return map.get(node.getId());
	}

	@Override
	public void put(INode node, IStyle style)
	{
		map.put(node.getId(), style);
	}

	@Override
	public void clear()
	{
		map.clear();
	}

}
