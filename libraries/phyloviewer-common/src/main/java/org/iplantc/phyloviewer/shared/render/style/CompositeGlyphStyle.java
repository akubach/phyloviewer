package org.iplantc.phyloviewer.shared.render.style;

/**
 * An IGlyphStyle that returns default values from a base IGlyphStyle if they haven't been set.
 */
public class CompositeGlyphStyle extends GlyphStyle
{
	private IGlyphStyle baseStyle;
	
	public CompositeGlyphStyle(IGlyphStyle baseStyle)
	{
		super(null, null, Double.NaN);
		this.baseStyle = baseStyle;
	}
	
	public String getFillColor() {
		if (fillColor != null)
		{
			return fillColor;
		}
		else
		{
			return baseStyle.getFillColor();
		}
	}

	public double getLineWidth() {
		if (strokeWidth != Double.NaN)
		{
			return strokeWidth;
		}
		else
		{
			return baseStyle.getLineWidth();
		}
	}

	public String getStrokeColor() {
		if (strokeColor != null)
		{
			return strokeColor;
		}
		else
		{
			return baseStyle.getStrokeColor();
		}
	}

	public void setBaseStyle(IGlyphStyle baseStyle)
	{
		this.baseStyle = baseStyle;
	}

	public IGlyphStyle getBaseStyle()
	{
		return baseStyle;
	}
}
