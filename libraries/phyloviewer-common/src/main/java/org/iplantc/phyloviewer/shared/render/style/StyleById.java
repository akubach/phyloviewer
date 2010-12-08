package org.iplantc.phyloviewer.shared.render.style;

import java.util.HashMap;

import org.iplantc.phyloviewer.shared.model.INode;

public class StyleById implements IStyleMap
{
	HashMap<String, IStyle> map = new HashMap<String, IStyle>();
	
	@Override
	public IStyle get(INode node)
	{
		return map.get(node.getStyleId());
	}

	@Override
	public void put(INode node, IStyle style)
	{
		map.put(node.getStyleId(), style);
	}

	@Override
	public void clear()
	{
		map.clear();
	}

}
