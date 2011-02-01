package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

public interface IDrawableBuilder
{
	/**
	 * 
	 * @param parent
	 * @param child
	 * @param layout
	 * @return
	 */
	public Drawable[] buildBranch(INode parent, INode child, ILayoutData layout);
	
	/**
	 * 
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable buildText(INode node, IDocument document, ILayoutData layout);
	
	/**
	 * 
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable[] buildNodeAbstraction(INode node, IDocument document, ILayoutData layout);
}
