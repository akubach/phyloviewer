package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BranchStyle implements IBranchStyle, IsSerializable
{
	String strokeColor = null;
	double strokeWidth = Double.NaN;

	public BranchStyle(String strokeColor, double strokeWidth)
	{
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
	}

	@Override
	public String getStrokeColor()
	{
		return this.strokeColor;
	}

	@Override
	public void setStrokeColor(String color)
	{
		this.strokeColor = color;
	}

	@Override
	public double getLineWidth()
	{
		return this.strokeWidth;
	}

	@Override
	public void setLineWidth(double width)
	{
		this.strokeWidth = width;
	}
}
