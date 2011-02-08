package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GlyphStyle implements IGlyphStyle, IsSerializable
{
	String fillColor = null;
	String strokeColor = null;
	double strokeWidth = Double.NaN;

	public GlyphStyle(String fillColor, String strokeColor, double strokeWidth)
	{
		this.fillColor = fillColor;
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
	}

	@Override
	public String getFillColor()
	{
		return fillColor;
	}

	@Override
	public double getLineWidth()
	{
		return strokeWidth;
	}

	@Override
	public String getStrokeColor()
	{
		return strokeColor;
	}

	@Override
	public void setFillColor(String color)
	{
		this.fillColor = color;
	}

	@Override
	public void setLineWidth(double width)
	{
		this.strokeWidth = width;
	}

	@Override
	public void setStrokeColor(String color)
	{
		this.strokeColor = color;
	}
}
