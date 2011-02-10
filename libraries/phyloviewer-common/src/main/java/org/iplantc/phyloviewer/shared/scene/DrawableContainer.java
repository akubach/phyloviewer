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

	public void clear()
	{
		nodeCache.clear();
		branchCache.clear();
		textCache.clear();
		glyphCache.clear();
	}

	/**
	 * Get the drawables for a node.
	 * 
	 * @param node
	 * @return array of drawables, null if the drawables haven't been created yet.
	 */
	public Drawable[] getNodeDrawables(INode node)
	{
		int id = node.getId();
		if(nodeCache.containsKey(id))
		{
			return nodeCache.get(id);
		}

		return null;
	}

	/**
	 * Get drawables for a node.
	 * 
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable[] getNodeDrawables(INode node, IDocument document, ILayoutData layout)
	{
		Drawable[] drawables = getNodeDrawables(node);
		if(drawables == null)
		{
			drawables = builder.buildNode(node, document, layout);
			nodeCache.put(node.getId(), drawables);
		}

		return drawables;
	}

	/**
	 * Get drawables to draw branches to the child.
	 * 
	 * @param child
	 * @return
	 */
	public Drawable[] getBranchDrawables(INode child)
	{
		int id = child.getId();
		if(branchCache.containsKey(id))
		{
			return branchCache.get(id);
		}

		return null;
	}

	/**
	 * Get drawables for a branch from parent to child.
	 * 
	 * @param parent
	 * @param child
	 * @param layout
	 * @return
	 */
	public Drawable[] getBranchDrawables(INode parent, INode child, IDocument document,
			ILayoutData layout)
	{
		Drawable[] drawables = getBranchDrawables(child);
		if(drawables == null)
		{
			int id = child.getId();
			drawables = builder.buildBranch(parent, child, document, layout);
			branchCache.put(id, drawables);
		}
		return drawables;
	}

	/**
	 * Get a drawable for the text.
	 * 
	 * @param node
	 * @return
	 */
	public Drawable getTextDrawable(INode node)
	{
		int id = node.getId();
		if(textCache.containsKey(id))
		{
			return textCache.get(id);
		}
		return null;
	}

	/**
	 * Get a drawable for the text.
	 * @param node
	 * @param document
	 * @param layout
	 * @return
	 */
	public Drawable getTextDrawable(INode node, IDocument document, ILayoutData layout)
	{
		Drawable drawable = getTextDrawable(node);
		if(drawable == null)
		{
			int id = node.getId();
			drawable = builder.buildText(node, document, layout);
			textCache.put(id, drawable);
		}
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
