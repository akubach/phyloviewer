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
		((CompositeNodeStyle)getNodeStyle()).setBaseStyle(baseStyle.getNodeStyle());
		((CompositeLabelStyle)getLabelStyle()).setBaseStyle(baseStyle.getLabelStyle());
		((CompositeGlyphStyle)getGlyphStyle()).setBaseStyle(baseStyle.getGlyphStyle());
		((CompositeBranchStyle)getBranchStyle()).setBaseStyle(baseStyle.getBranchStyle());
	}
	
	@Override
	public void setNodeStyle(INodeStyle baseStyle) {
		super.setNodeStyle(new CompositeNodeStyle(baseStyle));
	}
	
	@Override
	public void setLabelStyle(ILabelStyle baseStyle) {
		super.setLabelStyle(new CompositeLabelStyle(baseStyle));
	}
	
	@Override
	public void setGlyphStyle(IGlyphStyle baseStyle) {
		super.setGlyphStyle(new CompositeGlyphStyle(baseStyle));
	}
	
	@Override
	public void setBranchStyle(IBranchStyle baseStyle) {
		super.setBranchStyle(new CompositeBranchStyle(baseStyle));
	}
}
