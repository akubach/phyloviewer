package org.iplantc.phyloviewer.shared.scene;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

public class DrawableContainer
{
	private Map<Integer,Drawable[]> nodeCache = new HashMap<Integer,Drawable[]>();
	private Map<Integer,Drawable[]> branchCache = new HashMap<Integer,Drawable[]>();
	private Map<Integer,Drawable> textCache = new HashMap<Integer,Drawable>();
	private Map<Integer,Drawable[]> glyphCache = new HashMap<Integer,Drawable[]>();
	IDrawableBuilder builder;

	public DrawableContainer()
	{
	}
	
	public IDrawableBuilder getBuilder()
	{
		return builder;
	}

	public void setBuilder(IDrawableBuilder builder)
	{
		this.builder = builder;
		clear();
	}

	private void clear()
	{
		nodeCache.clear();
		branchCache.clear();
		textCache.clear();
		glyphCache.clear();
	}

	/**
	 * Get drawables for a node.
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable[] getNodeDrawables(INode node, IDocument document, ILayoutData layout)
	{
		int id = node.getId();
		if(nodeCache.containsKey(id))
		{
			return nodeCache.get(id);
		}
		
		Drawable[] drawables = builder.buildNode(node, document, layout);
		nodeCache.put(id, drawables);
		return drawables;
	}
	
	/**
	 * Get drawables for a branch from parent to child.
	 * @param parent
	 * @param child
	 * @param layout
	 * @return
	 */
	public Drawable[] getBranchDrawables(INode parent, INode child, ILayoutData layout)
	{
		int id = child.getId();
		if(branchCache.containsKey(id))
		{
			return branchCache.get(id);
		}
		
		Drawable[] drawables = builder.buildBranch(parent, child, layout);
		branchCache.put(id, drawables);
		return drawables;
	}
	
	/**
	 * 
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable getTextDrawable(INode node, IDocument document, ILayoutData layout)
	{
		int id = node.getId();
		if(textCache.containsKey(id))
		{
			return textCache.get(id);
		}
		
		Drawable drawable = builder.buildText(node, document, layout);
		textCache.put(id, drawable);
		return drawable;
	}

	/**
	 * 
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable[] getGlyphDrawables(INode node, IDocument document, ILayoutData layout)
	{
		int id = node.getId();
		if(glyphCache.containsKey(id))
		{
			return glyphCache.get(id);
		}
		
		Drawable[] drawables = builder.buildNodeAbstraction(node, document, layout);
		glyphCache.put(id, drawables);
		return drawables;
	}
}
