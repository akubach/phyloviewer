package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView.RequestRenderCallback;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle.Element;


public class RenderTreeCladogram extends RenderTree {

	protected void drawLabel(INode node, ILayout layout, IGraphics graphics) {
		Vector2 position = layout.getPosition(node);
		this.drawLabel(node, graphics, position);
	}

	protected boolean canDrawChildLabels(INode node, ILayout layout, IGraphics graphics) {
		Box2D boundingBox = layout.getBoundingBox(node);
		return estimateNumberOfPixelsNeeded(node) < graphics.getDisplayedBox(boundingBox).getHeight();
	}

	protected void renderPlaceholder(INode node, ILayout layout,
			IGraphics graphics) {
		Box2D boundingBox = layout.getBoundingBox(node);
		Vector2 min = boundingBox.getMin();
		Vector2 max = boundingBox.getMax();

		setStyle(node, graphics, Element.GLYPH);
		graphics.drawTriangle(layout.getPosition(node),max.getX(),min.getY(),max.getY());

		this.drawLabel(node, graphics, new Vector2(max.getX(),(min.getY()+max.getY())/2.0));
	}
	
	private void drawLabel(INode node, IGraphics graphics, Vector2 position) {
		if (getLabel(node) != null)
		{
			setStyle(node, graphics, Element.LABEL);
			Vector2 offset = new Vector2(7,2);
			graphics.drawText(position, offset, getLabel(node));
		}
	}

	protected void renderChildren(INode node, ILayout layout, IGraphics graphics, RequestRenderCallback renderCallback) 
	{
		INode[] children = node.getChildren();
		for ( int i = 0; i < children.length; ++i ) {

			INode child = children[i];
			setStyle(child, graphics, Element.BRANCH);
			graphics.drawRightAngle(layout.getPosition(node), layout.getPosition(child));
			
			renderNode(child, layout, graphics, renderCallback);
		}
	}

	protected double estimateNumberOfPixelsNeeded(INode node) {
		int numberOfLeafNodes = node.getNumberOfChildren();
		int pixelsPerTaxon = 15;
		return numberOfLeafNodes * pixelsPerTaxon;
	}
}
