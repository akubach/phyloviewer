package org.iplantc.phyloviewer.shared.render.style;


public interface INodeStyle {
	
	public abstract String getColor();
	public abstract void setColor(String color);
	public abstract double getPointSize();
	public abstract void setPointSize(double size);
}
