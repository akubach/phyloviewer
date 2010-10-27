package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import java.util.EnumMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NodeStyle implements INodeStyle, IsSerializable {
	EnumMap<Element, IElementStyle> elementStyles = new EnumMap<Element, IElementStyle>(Element.class);
	
	public NodeStyle() 
	{
		for (Element element : Element.values()) 
		{
			elementStyles.put(element, new ElementStyle());
		}
	}
	
	public NodeStyle(String nodeStrokeColor, String nodeFillColor, double nodeLineWidth, 
			String branchStrokeColor, String branchFillColor, double branchLineWidth, 
			String glyphStrokeColor, String glyphFillColor, double glyphLineWidth, 
			String labelStrokeColor, String labelFillColor, double labelLineWidth)
	{
		this();
		elementStyles.get(Element.NODE).setStrokeColor(nodeStrokeColor);
		elementStyles.get(Element.NODE).setFillColor(nodeFillColor);
		elementStyles.get(Element.NODE).setLineWidth(nodeLineWidth);
		elementStyles.get(Element.BRANCH).setStrokeColor(branchStrokeColor);
		elementStyles.get(Element.BRANCH).setFillColor(branchFillColor);
		elementStyles.get(Element.BRANCH).setLineWidth(branchLineWidth);
		elementStyles.get(Element.GLYPH).setStrokeColor(glyphStrokeColor);
		elementStyles.get(Element.GLYPH).setFillColor(glyphFillColor);
		elementStyles.get(Element.GLYPH).setLineWidth(glyphLineWidth);
		elementStyles.get(Element.LABEL).setStrokeColor(labelStrokeColor);
		elementStyles.get(Element.LABEL).setFillColor(labelFillColor);
		elementStyles.get(Element.LABEL).setLineWidth(labelLineWidth);
	}

	@Override
	public IElementStyle getElementStyle(Element element) 
	{
		return elementStyles.get(element);
	}
}
