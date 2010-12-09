package org.iplantc.phyloviewer.shared.render.style;

public interface IBranchStyle {

	public abstract String getStrokeColor();
	public abstract void setStrokeColor(String color);
	public abstract double getLineWidth();
	public abstract void setLineWidth(double width);
}
