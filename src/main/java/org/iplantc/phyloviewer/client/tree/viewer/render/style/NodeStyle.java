package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import java.util.EnumMap;

public class NodeStyle implements INodeStyle {
	EnumMap<Element, IElementStyle> elementStyles = new EnumMap<Element, IElementStyle>(Element.class);
	
	public NodeStyle() {
		for (Element element : Element.values()) {
			elementStyles.put(element, new ElementStyle());
		}
	}

	@Override
	public IElementStyle getElementStyle(Element element) {
		return elementStyles.get(element);
	}
}
