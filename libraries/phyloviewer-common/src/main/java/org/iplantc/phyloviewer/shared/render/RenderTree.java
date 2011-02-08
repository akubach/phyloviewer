/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import java.util.Stack;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.scene.Drawable;
import org.iplantc.phyloviewer.shared.scene.DrawableContainer;
import org.iplantc.phyloviewer.shared.scene.IDrawableBuilder;
import org.iplantc.phyloviewer.shared.scene.ILODSelector;
import org.iplantc.phyloviewer.shared.scene.ILODSelector.LODLevel;

public abstract class RenderTree
{
	private RenderPreferences renderPreferences = new RenderPreferences();
	IDocument document;
	IDrawableBuilder builder;
	ILODSelector lodSelector;
	DrawableContainer drawableContainer = new DrawableContainer();
	Stack<Boolean> highlightSubTreeStack = new Stack<Boolean>();

	public RenderTree()
	{
	}

	public IDocument getDocument()
	{
		return document;
	}

	public void setDocument(IDocument document)
	{
		this.document = document;
		drawableContainer.clear();
	}

	protected void setDrawableBuilder(IDrawableBuilder builder)
	{
		this.builder = builder;
		drawableContainer.setBuilder(builder);
	}

	protected void setLODSelector(ILODSelector lodSelector)
	{
		this.lodSelector = lodSelector;
	}

	public DrawableContainer getDrawableContainer()
	{
		return drawableContainer;
	}

	public void renderTree(IGraphics graphics, Camera camera)
	{
		ITree tree = document != null ? document.getTree() : null;
		ILayoutData layout = document != null ? document.getLayout() : null;

		if(document == null || tree == null || graphics == null || layout == null || builder == null
				|| lodSelector == null)
			return;

		INode root = tree.getRootNode();

		if(root == null)
			return;

		if(camera != null)
		{
			graphics.setViewMatrix(camera.getMatrix(graphics.getWidth(), graphics.getHeight()));
		}

		graphics.clear();

		highlightSubTreeStack.clear();
		highlightSubTreeStack.push(false);

		this.renderNode(root, layout, graphics);
	}

	public RenderPreferences getRenderPreferences()
	{
		return renderPreferences;
	}

	public void setRenderPreferences(RenderPreferences preferences)
	{
		renderPreferences = preferences;
	}

	protected void renderNode(INode node, ILayoutData layout, IGraphics graphics)
	{
		if(graphics.isCulled(this.getBoundingBox(node, layout)))
		{
			return;
		}

		boolean stackNeedsPopped = false;
		if(renderPreferences.isSubTreeHighlighted(node))
		{
			highlightSubTreeStack.push(true);
			stackNeedsPopped = true;
		}

		if(renderPreferences.drawLabels() && node.isLeaf())
		{
			drawLabel(node, layout, graphics);
		}
		else if(renderPreferences.isCollapsed(node)
				|| (renderPreferences.collapseOverlaps() && LODLevel.LOD_LOW == lodSelector.getLODLevel(
						node, layout, graphics.getViewMatrix())))
		{
			renderPlaceholder(node, layout, graphics);
		}
		else if(!document.checkForData(node))
		{
			// while checkForData gets children and layouts (async), render a subtree placeholder
			renderPlaceholder(node, layout, graphics);
		}
		else
		{
			renderChildren(node, layout, graphics);
		}

		if(renderPreferences.isDrawPoints())
		{
			boolean isHighlighted = renderPreferences.isNodeHighlighted(node);
			IStyle style = this.getStyle(node, isHighlighted);
			Drawable[] drawables = drawableContainer.getNodeDrawables(node, document, layout);
			for(Drawable drawable : drawables)
			{
				drawable.draw(graphics, style);
			}
		}

		if(stackNeedsPopped)
		{
			highlightSubTreeStack.pop();
		}
	}

	public Box2D getBoundingBox(INode node, ILayoutData layout)
	{
		return layout.getBoundingBox(node);
	}

	protected void drawLabel(INode node, ILayoutData layout, IGraphics graphics)
	{
		IStyle style = this.getStyle(node, false);
		Drawable drawable = drawableContainer.getTextDrawable(node, document, layout);
		drawable.draw(graphics, style);
	}

	protected void renderChildren(INode parent, ILayoutData layout, IGraphics graphics)
	{
		INode[] children = parent.getChildren();
		for(int i = 0;i < children.length;++i)
		{
			INode child = children[i];

			boolean isHighlighted = renderPreferences.isBranchHighlighted(child);
			IStyle style = this.getStyle(child, isHighlighted);

			Drawable[] drawables = drawableContainer.getBranchDrawables(parent, child, document, layout);
			for(Drawable drawable : drawables)
			{
				drawable.draw(graphics, style);
			}

			renderNode(child, layout, graphics);
		}
	}

	protected void renderPlaceholder(INode node, ILayoutData layout, IGraphics graphics)
	{
		IStyle style = this.getStyle(node, false);
		Drawable[] drawables = drawableContainer.getGlyphDrawables(node, document, layout);
		for(Drawable drawable : drawables)
		{
			drawable.draw(graphics, style);
		}
	}

	/**
	 * If the node has been highlighted, the returned style will be renderPreferences.getHighlightStyle()
	 * composited with the node style
	 * 
	 * @see CompositeStyle
	 */
	protected IStyle getStyle(INode node, boolean isHighlighted)
	{
		assert (document != null);
		IStyle style = document.getStyle(node);

		if(isHighlighted || highlightSubTreeStack.peek())
		{
			CompositeStyle highlightStyle = renderPreferences.getHighlightStyle();
			if(highlightStyle != null)
			{
				highlightStyle.setBaseStyle(style);
				style = highlightStyle;
			}
		}

		return style;
	}
}
