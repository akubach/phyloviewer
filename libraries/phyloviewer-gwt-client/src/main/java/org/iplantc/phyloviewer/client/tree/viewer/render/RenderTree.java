/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

public abstract class RenderTree {
	private RenderPreferences renderPreferences = new RenderPreferences();
	IDocument document;
	
	public RenderTree() {
	}
	
	public RenderTree(IDocument document) {
		this.document = document;
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void renderTree(IGraphics graphics, Camera camera) {
		ITree tree = document != null ? document.getTree() : null;
		ILayoutData layout = document != null ? document.getLayout() : null;
		
		if ( document == null || tree == null || graphics == null || layout == null)
			return;
		
		INode root = tree.getRootNode();
		
		if ( root == null )
			return;
		
		if (camera!=null){
			graphics.setViewMatrix(camera.getMatrix(graphics.getWidth(),graphics.getHeight()));
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
	
	protected void renderNode(INode node, ILayoutData layout, IGraphics graphics) {

		if ( graphics.isCulled(this.getBoundingBox(node,layout))) {
			return;
		}
		
		if (renderPreferences.drawLabels() && node.isLeaf()) {
			drawLabel(node, layout, graphics);
		} else if (renderPreferences.collapseOverlaps() && !canDrawChildLabels(node, layout, graphics)) {
			renderPlaceholder(node, layout, graphics);
		} else if (!document.checkForData(node)) {
			//while checkForData gets children and layouts (async), render a subtree placeholder
			renderPlaceholder(node, layout, graphics);
		} else {
			renderChildren(node, layout, graphics);
		}
		
		graphics.setStyle(this.getStyle(node).getNodeStyle());
		graphics.drawPoint(this.getPosition(node,layout)); 
	}
	
	protected Vector2 getPosition(INode node,ILayoutData layout) {
		return layout.getPosition(node);
	}
	
	public Box2D getBoundingBox(INode node,ILayoutData layout) {
		return layout.getBoundingBox(node);
	}
	
	protected abstract void drawLabel(INode node, ILayoutData layout, IGraphics graphics);
	protected abstract void renderChildren(INode node, ILayoutData layout, IGraphics graphics);
	protected abstract void renderPlaceholder(INode node, ILayoutData layout, IGraphics graphics);

	/**
	 * Styling is done in layers: default style, node style, user style, highlight style
	 */
	protected IStyle getStyle(INode node) {
		
		IStyle highlightStyle = renderPreferences.getHighlightStyle();
		if(renderPreferences.isHighlighted(node) && highlightStyle != null) {
			return highlightStyle;
		}
		
		assert(document!=null);
		return document.getStyle(node);
	}
	
	protected double estimateNumberOfPixelsNeeded(INode node) {
		int numberOfLeafNodes = node.getNumberOfChildren();
		int pixelsPerTaxon = 15;
		return numberOfLeafNodes * pixelsPerTaxon;
	}
	
	protected boolean canDrawChildLabels(INode node, ILayoutData layout, IGraphics graphics) {
		Box2D boundingBox = layout.getBoundingBox(node);
		return estimateNumberOfPixelsNeeded(node) < graphics.getDisplayedBox(boundingBox).getHeight();
	}
}
