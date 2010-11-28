package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import java.util.EnumMap;

import org.iplantc.phyloviewer.shared.render.style.ElementStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle.Element;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle.IElementStyle;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NodeStyle extends EnumMap<Element, IElementStyle> implements INodeStyle, IsSerializable {
	private static final long serialVersionUID = 5623631752585978963L;
	
	public NodeStyle() 
	{
		super(Element.class);
		
		for (Element element : Element.values()) 
		{
			this.put(element, new ElementStyle());
		}
	}
	
	public NodeStyle(String nodeStrokeColor, String nodeFillColor, double nodeLineWidth, 
			String branchStrokeColor, String branchFillColor, double branchLineWidth, 
			String glyphStrokeColor, String glyphFillColor, double glyphLineWidth, 
			String labelStrokeColor, String labelFillColor, double labelLineWidth)
	{
		this();
		this.get(Element.NODE).setStrokeColor(nodeStrokeColor);
		this.get(Element.NODE).setFillColor(nodeFillColor);
		this.get(Element.NODE).setLineWidth(nodeLineWidth);
		this.get(Element.BRANCH).setStrokeColor(branchStrokeColor);
		this.get(Element.BRANCH).setFillColor(branchFillColor);
		this.get(Element.BRANCH).setLineWidth(branchLineWidth);
		this.get(Element.GLYPH).setStrokeColor(glyphStrokeColor);
		this.get(Element.GLYPH).setFillColor(glyphFillColor);
		this.get(Element.GLYPH).setLineWidth(glyphLineWidth);
		this.get(Element.LABEL).setStrokeColor(labelStrokeColor);
		this.get(Element.LABEL).setFillColor(labelFillColor);
		this.get(Element.LABEL).setLineWidth(labelLineWidth);
	}

	@Override
	public IElementStyle getElementStyle(Element element) 
	{
		return this.get(element);
	}
}
