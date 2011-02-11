package org.iplantc.phyloviewer.shared.render.style;

public interface IStyle
{
	/**
	 * Get the id for this style.
	 * @return
	 */
	public abstract String getId();

	/**
	 * Get the style for the node.
	 * @return
	 */
	public abstract INodeStyle getNodeStyle();

	/**
	 * Get the style for the label.
	 * @return
	 */
	public abstract ILabelStyle getLabelStyle();

	/**
	 * Get the style for the glyph.
	 * @return
	 */
	public abstract IGlyphStyle getGlyphStyle();

	/**
	 * Get the style for the branch.
	 * @return
	 */
	public abstract IBranchStyle getBranchStyle();
}
