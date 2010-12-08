package org.iplantc.phyloviewer.shared.render.style;

public interface IStyle {

	public abstract String getId();
	
	public abstract INodeStyle getNodeStyle();
	public abstract ILabelStyle getLabelStyle();
	public abstract IGlyphStyle getGlyphStyle();
	public abstract IBranchStyle getBranchStyle();
}
