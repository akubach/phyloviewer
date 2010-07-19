package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.Defaults;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.Element;

public class StyleMap extends IStyleMap {
	private ColorForNode[] fillColorMap;
	private ColorForNode[] strokeColorMap;
	private DoubleForNode[] lineWidthMap;
	
	public StyleMap() {
		//it's possible (but unlikely) that the Element enum constants won't start at zero or be consecutive, so we'll find the max just in case
		int maxIndex = 0;
		for (Element element : Element.values()) {
			maxIndex = Math.max(element.ordinal(), maxIndex);
		}
		fillColorMap = new ColorForNode[maxIndex + 1];
		strokeColorMap = new ColorForNode[maxIndex + 1];
		lineWidthMap = new DoubleForNode[maxIndex + 1];
		
		//set defaults
		setFillStyle(Element.NODE, createConstantColorForNode(Defaults.POINT_COLOR));
		setStrokeStyle(Element.NODE, createConstantColorForNode(Defaults.POINT_COLOR));
		setLineWidth(Element.NODE, createConstantDoubleForNode(1.0));
		
		setFillStyle(Element.GLYPH, createConstantColorForNode(Defaults.TRIANGLE_FILL_COLOR));
		setStrokeStyle(Element.GLYPH, createConstantColorForNode(Defaults.TRIANGLE_OUTLINE_COLOR));
		setLineWidth(Element.GLYPH, createConstantDoubleForNode(1.0));
		
		setFillStyle(Element.BRANCH, createConstantColorForNode(Defaults.LINE_COLOR));
		setStrokeStyle(Element.BRANCH, createConstantColorForNode(Defaults.LINE_COLOR));
		setLineWidth(Element.BRANCH, createConstantDoubleForNode(1.0));
		
		setFillStyle(Element.LABEL, createConstantColorForNode(Defaults.TEXT_COLOR));
		setStrokeStyle(Element.LABEL, createConstantColorForNode(Defaults.TEXT_COLOR));
		setLineWidth(Element.LABEL, createConstantDoubleForNode(1.0));
	}
	
	@Override
	public String getFillStyle(Element element, INode node) {
		return fillColorMap[element.ordinal()].getColor(node);
	}

	@Override
	public double getLineWidth(Element element, INode node) {
		return lineWidthMap[element.ordinal()].getDouble(node);
	}

	@Override
	public String getStrokeStyle(Element element, INode node) {
		return strokeColorMap[element.ordinal()].getColor(node);
	}
	
	public void setFillStyle(Element element, ColorForNode colorMap) {
		fillColorMap[element.ordinal()] = colorMap;
	}
	
	public void setLineWidth(Element element, DoubleForNode widthMap) {
		lineWidthMap[element.ordinal()] = widthMap;
	}
	
	public void setStrokeStyle(Element element, ColorForNode colorMap) {
		strokeColorMap[element.ordinal()] = colorMap;
	}

	public static ColorForNode createConstantColorForNode(final String color) {
		return new ColorForNode() {
			public String getColor(INode node) {
				return color;
			}
		};
	}
	
	public static DoubleForNode createConstantDoubleForNode(final double d) {
		return new DoubleForNode() {
			public double getDouble(INode node) {
				return 1.0;
			}
		};
	}
	
	public interface ColorForNode {
		public String getColor(INode node);
	}
	
	public interface DoubleForNode {
		public double getDouble(INode node);
	}
}
