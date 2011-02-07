package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

public interface IDrawableBuilder
{
	/**
	 * Build drawables for a node.
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable[] buildNode(INode node, IDocument document, ILayoutData layout);
	
	/**
	 * Build drawables for a branch from parent to child.
	 * @param parent
	 * @param child
	 * @param layout
	 * @return
	 */
	public Drawable[] buildBranch(INode parent, INode child, IDocument document, ILayoutData layout);
	
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
