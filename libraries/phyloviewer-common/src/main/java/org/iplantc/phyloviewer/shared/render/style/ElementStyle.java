package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ElementStyle implements IsSerializable {
	String fillColor = null;
	String strokeColor = null;
	double strokeWidth = Double.NaN;
	
	public ElementStyle(String fillColor,String strokeColor,double strokeWidth) {
		this.fillColor = fillColor;
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
	}
	
	public String getFillColor() {
		return fillColor;
	}

	public double getLineWidth() {
		return strokeWidth;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setFillColor(String color) {
		this.fillColor = color;
	}

	public void setLineWidth(double width) {
		this.strokeWidth = width;
	}

	public void setStrokeColor(String color) {
		this.strokeColor = color;
	}
}
