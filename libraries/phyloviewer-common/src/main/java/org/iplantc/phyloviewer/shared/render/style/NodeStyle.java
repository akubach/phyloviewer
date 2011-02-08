package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NodeStyle implements INodeStyle, IsSerializable
{
	String color = null;
	double pointSize = Double.NaN;

	public NodeStyle(String color, double pointSize)
	{
		this.color = color;
		this.pointSize = pointSize;
	}

	@Override
	public String getColor()
	{
		return this.color;
	}

	@Override
	public void setColor(String color)
	{
		this.color = color;
	}

	@Override
	public double getPointSize()
	{
		return this.pointSize;
	}

	@Override
	public void setPointSize(double size)
	{
		this.pointSize = size;
	}
}
