package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LabelStyle implements ILabelStyle, IsSerializable
{
	String color = null;

	public LabelStyle(String color)
	{
		this.color = color;
	}

	@Override
	public String getColor()
	{
		return color;
	}

	@Override
	public void setColor(String color)
	{
		this.color = color;
	}
}
