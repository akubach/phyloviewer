package org.iplantc.phyloviewer.shared.render.style;

public interface IElementStyle {
	
	public abstract String getStrokeColor();
	public abstract void setStrokeColor(String color);
	public abstract String getFillColor();
	public abstract void setFillColor(String color);
	public abstract double getLineWidth();
	public abstract void setLineWidth(double width);

}