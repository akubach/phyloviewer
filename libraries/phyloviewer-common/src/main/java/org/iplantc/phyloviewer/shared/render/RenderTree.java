/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.scene.Drawable;
import org.iplantc.phyloviewer.shared.scene.IDrawableBuilder;
import org.iplantc.phyloviewer.shared.scene.ILODSelector;
import org.iplantc.phyloviewer.shared.scene.ILODSelector.LODLevel;

public abstract class RenderTree
{
	private RenderPreferences renderPreferences = new RenderPreferences();
	IDocument document;
	IDrawableBuilder builder;
	ILODSelector lodSelector;

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
	}

	protected void setDrawableBuilder(IDrawableBuilder builder)
	{
		this.builder = builder;
	}

	protected void setLODSelector(ILODSelector lodSelector)
	{
		this.lodSelector = lodSelector;
	}

	public void renderTree(IGraphics graphics, Camera camera)
	{
		ITree tree = document != null ? document.getTree() : null;
		ILayoutData layout = document != null ? document.getLayout() : null;

		if(document == null || tree == null || graphics == null || layout == null || builder == null)
			return;

		INode root = tree.getRootNode();

		if(root == null)
			return;

		if(camera != null)
		{
			graphics.setViewMatrix(camera.getMatrix(graphics.getWidth(), graphics.getHeight()));
		}

		graphics.clear();

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

		graphics.setStyle(this.getStyle(node).getNodeStyle());
		graphics.drawPoint(this.getPosition(node, layout));
	}

	protected Vector2 getPosition(INode node, ILayoutData layout)
	{
		return layout.getPosition(node);
	}

	public Box2D getBoundingBox(INode node, ILayoutData layout)
	{
		return layout.getBoundingBox(node);
	}

	protected void drawLabel(INode node, ILayoutData layout, IGraphics graphics)
	{
		graphics.setStyle(this.getStyle(node).getLabelStyle());
		Drawable drawable = builder.buildText(node, document, layout);
		drawable.draw(graphics);
	}

	protected void renderChildren(INode node, ILayoutData layout, IGraphics graphics)
	{
		INode[] children = node.getChildren();
		for(int i = 0;i < children.length;++i)
		{
			INode child = children[i];
			Drawable[] drawables = builder.buildBranch(node, child, layout);
			graphics.setStyle(this.getStyle(child).getBranchStyle());
			for(Drawable drawable : drawables)
			{
				drawable.draw(graphics);
			}

			renderNode(child, layout, graphics);
		}
	}

	protected void renderPlaceholder(INode node, ILayoutData layout, IGraphics graphics)
	{
		graphics.setStyle(this.getStyle(node).getGlyphStyle());
		graphics.setStyle(this.getStyle(node).getLabelStyle());

		Drawable[] drawables = builder.buildNodeAbstraction(node, document, layout);
		for(Drawable drawable : drawables)
		{
			drawable.draw(graphics);
		}
	}

	/**
	 * If the node has been highlighted, the returned style will be renderPreferences.getHighlightStyle()
	 * composited with the node style
	 * 
	 * @see CompositeStyle
	 */
	protected IStyle getStyle(INode node)
	{
		assert (document != null);
		IStyle style = document.getStyle(node);

		if(renderPreferences.isHighlighted(node))
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
