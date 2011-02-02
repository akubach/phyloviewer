package org.iplantc.phyloviewer.shared.render.style;

/**
 * An IBranchStyle that returns default values from a base IBranchStyle if they haven't been set.
 */
public class CompositeBranchStyle extends BranchStyle
{
	private IBranchStyle baseStyle;	
	
	public CompositeBranchStyle(IBranchStyle baseStyle)
	{
		super(null, Double.NaN);
		this.baseStyle = baseStyle;
	}
	
	@Override
	public String getStrokeColor() 
	{
		if (strokeColor != null)
		{
			return strokeColor;
		}
		else
		{
			return baseStyle.getStrokeColor();
		}
	}

	@Override
	public double getLineWidth() 
	{
		if (!Double.isNaN(strokeWidth))
		{
			return strokeWidth;
		}
		else
		{
			return baseStyle.getLineWidth();
		}
	}

	public void setBaseStyle(IBranchStyle baseStyle)
	{
		this.baseStyle = baseStyle;
	}

	public IBranchStyle getBaseStyle()
	{
		return baseStyle;
	}
}
