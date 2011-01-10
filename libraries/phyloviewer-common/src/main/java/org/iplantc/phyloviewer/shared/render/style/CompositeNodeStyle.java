package org.iplantc.phyloviewer.shared.render.style;

/**
 * An INodeStyle that returns default values from a base INodeStyle if they haven't been set.
 */
public class CompositeNodeStyle extends NodeStyle
{
	INodeStyle baseStyle;
	
	public CompositeNodeStyle(INodeStyle baseStyle)
	{
		super(null, Double.NaN);
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

	@Override
	public double getPointSize() {
		if (!Double.isNaN(pointSize))
		{
			return pointSize;
		}
		else
		{
			return baseStyle.getPointSize();
		}
	}
}
