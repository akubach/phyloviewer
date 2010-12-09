package org.iplantc.phyloviewer.shared.render.style;

import java.util.HashMap;
import java.util.Set;

import org.iplantc.phyloviewer.shared.model.INode;

public class StyleById implements IStyleMap
{
	HashMap<String, IStyle> map = new HashMap<String, IStyle>();
	
	@Override
	public IStyle get(INode node)
	{
		if(node!=null) {
			return this.get(node.getStyleId());
		}
		
		return null;
	}
	
	public IStyle get(String key) {
		return map.get(key);
	}

	@Override
	public void put(INode node, IStyle style)
	{
		if(node!=null) {
			this.put(node.getStyleId(), style);
		}
	}
	
	public void put(String styleId,IStyle style) {
		map.put(styleId, style);
	}

	@Override
	public void clear()
	{
		map.clear();
	}

	public Set<String> getKeys() {
		return map.keySet();
	}
}
