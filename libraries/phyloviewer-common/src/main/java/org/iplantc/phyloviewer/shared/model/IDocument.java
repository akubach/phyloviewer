package org.iplantc.phyloviewer.shared.model;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public interface IDocument
{
	/**
	 * Get the tree.
	 * @return
	 */
	public abstract ITree getTree();

	/**
	 * Get the style map.
	 * @return
	 */
	public abstract IStyleMap getStyleMap();

	/**
	 * Set the style map.
	 * @param styleMap
	 */
	public abstract void setStyleMap(IStyleMap styleMap);

	/**
	 * Get the style for the node.
	 * @param node
	 * @return
	 */
	public abstract IStyle getStyle(INode node);

	/**
	 * Get the lable to use for the node.
	 * @param node
	 * @return
	 */
	public abstract String getLabel(INode node);

	/**
	 * Get the layout data.
	 * @return
	 */
	public abstract ILayoutData getLayout();
	
	public abstract boolean hasBranchDecoration(int nodeId);

	/**
	 * Is all the data for the node local?
	 * @param node
	 * @return
	 */
	public abstract boolean checkForData(final INode node);

	/**
	 * Is the document ready to begin rendering?
	 * @return
	 */
	public abstract boolean isReady();
}