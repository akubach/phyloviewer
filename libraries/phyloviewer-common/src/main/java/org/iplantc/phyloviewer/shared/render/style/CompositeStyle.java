package org.iplantc.phyloviewer.shared.render.style;

/**
 * A style based on an existing style. Returns values from the base style if the value hasn't been set.
 * 
 * Wraps element styles (NodeStyle, BranchStyle, etc) in a corresponding composite element style, so modifying the
 * element styles returned by getters does not modify the base style.
 */
public class CompositeStyle extends Style
{
	public CompositeStyle(String id, IStyle baseStyle)
	{
		super(id, new CompositeNodeStyle(baseStyle.getNodeStyle()), 
				new CompositeLabelStyle(baseStyle.getLabelStyle()), 
				new CompositeGlyphStyle(baseStyle.getGlyphStyle()), 
				new CompositeBranchStyle(baseStyle.getBranchStyle()));
	}
	
	public void setBaseStyle(IStyle baseStyle)
	{
		//set element base styles
		getNodeStyle().setBaseStyle(baseStyle.getNodeStyle());
		getLabelStyle().setBaseStyle(baseStyle.getLabelStyle());
		getGlyphStyle().setBaseStyle(baseStyle.getGlyphStyle());
		getBranchStyle().setBaseStyle(baseStyle.getBranchStyle());
	}
	
	@Override
	public void setNodeStyle(INodeStyle baseStyle) {
		getNodeStyle().setColor(baseStyle.getColor());
		getNodeStyle().setPointSize(baseStyle.getPointSize());
	}
	
	@Override
	public void setLabelStyle(ILabelStyle baseStyle) {
		getLabelStyle().setColor(baseStyle.getColor());
	}
	
	@Override
	public void setGlyphStyle(IGlyphStyle baseStyle) {
		getGlyphStyle().setFillColor(baseStyle.getFillColor());
		getGlyphStyle().setLineWidth(baseStyle.getLineWidth());
		getGlyphStyle().setStrokeColor(baseStyle.getStrokeColor());
	}
	
	@Override
	public void setBranchStyle(IBranchStyle baseStyle) {
		getBranchStyle().setLineWidth(baseStyle.getLineWidth());
		getBranchStyle().setStrokeColor(baseStyle.getStrokeColor());
	}

	@Override
	public CompositeBranchStyle getBranchStyle()
	{
		return (CompositeBranchStyle) super.getBranchStyle();
	}

	@Override
	public CompositeGlyphStyle getGlyphStyle()
	{
		return (CompositeGlyphStyle) super.getGlyphStyle();
	}

	@Override
	public CompositeLabelStyle getLabelStyle()
	{
		return (CompositeLabelStyle) super.getLabelStyle();
	}

	@Override
	public CompositeNodeStyle getNodeStyle()
	{
		return (CompositeNodeStyle) super.getNodeStyle();
	}
	
	
}
