package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.IElementStyle;

public class ElementStyle implements IElementStyle {
	String fillColor = null;
	String strokeColor = null;
	double strokeWidth = Double.NaN;
	
	@Override
	public String getFillColor() {
		return fillColor;
	}

	@Override
	public double getLineWidth() {
		return strokeWidth;
	}

	@Override
	public String getStrokeColor() {
		return strokeColor;
	}

	@Override
	public void setFillColor(String color) {
		this.fillColor = color;

	}

	@Override
	public void setLineWidth(double width) {
		this.strokeWidth = width;
	}

	@Override
	public void setStrokeColor(String color) {
		this.strokeColor = color;
	}
}
