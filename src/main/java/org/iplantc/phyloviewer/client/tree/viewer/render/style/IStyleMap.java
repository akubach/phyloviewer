package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

/**
 * An IStyleMap calculates per-node styling info for each graphical element of
 * the tree (nodes, branches, labels, glyphs)
 */
public interface IStyleMap {
	
	/** 
	 * @return a stroke style string for the given element of the given node in the format used by Canvas
	 * @see org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas#setStrokeStyle(String)
	 * @see INodeStyle.Element
	 */
	public abstract String getStrokeStyle(INodeStyle.Element element, INode node);
	
	/** 
	 * @return a fill style string for the given element of the given node in the format used by Canvas
	 * @see org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas#setFillStyle(String)
	 * @see INodeStyle.Element
	 */
	public abstract String getFillStyle(INodeStyle.Element element, INode node);
	
	/** 
	 * @return a stroke width for the given element of the given node (but mostly for branches).
	 * @see INodeStyle.Element
	 */
	public abstract double getLineWidth(INodeStyle.Element element, INode node);
}
