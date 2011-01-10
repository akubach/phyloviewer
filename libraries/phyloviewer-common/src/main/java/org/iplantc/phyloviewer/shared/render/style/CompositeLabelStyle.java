package org.iplantc.phyloviewer.shared.render.style;

/**
 * An ILabelStyle that returns default values from a base ILabelStyle if they haven't been set.
 */
public class CompositeLabelStyle extends LabelStyle
{
	ILabelStyle baseStyle;
	
	public CompositeLabelStyle(ILabelStyle baseStyle)
	{
		super(null);
		this.baseStyle = baseStyle;
	}
	
	@Override
	public String getColor() {
		if (color != null)
		{
			return color;
		}
		else
		{
			return baseStyle.getColor();
		}
	}
}
