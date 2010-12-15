package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView.RequestRenderCallback;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.IGraphics;


public class RenderTreeCladogram extends RenderTree {

	public RenderTreeCladogram() {
	}
	
	public RenderTreeCladogram(IDocument document) {
		super(document);
	}
	
	protected void drawLabel(INode node, ILayout layout, IGraphics graphics) {
		Vector2 position = layout.getPosition(node);
		this.drawLabel(node, graphics, position);
	}

	protected void renderPlaceholder(INode node, ILayout layout,
			IGraphics graphics) {
		Box2D boundingBox = layout.getBoundingBox(node);
		Vector2 min = boundingBox.getMin();
		Vector2 max = boundingBox.getMax();

		graphics.setStyle(this.getStyle(node).getGlyphStyle());
		graphics.drawTriangle(layout.getPosition(node),max.getX(),min.getY(),max.getY());

		this.drawLabel(node, graphics, new Vector2(max.getX(),(min.getY()+max.getY())/2.0));
	}
	
	private void drawLabel(INode node, IGraphics graphics, Vector2 position) {
		if (document.getLabel(node) != null)
		{
			graphics.setStyle(this.getStyle(node).getLabelStyle());
			Vector2 offset = new Vector2(7,2);
			graphics.drawText(position, offset, document.getLabel(node));
		}
	}

	protected void renderChildren(INode node, ILayout layout, IGraphics graphics, RequestRenderCallback renderCallback) 
	{
		INode[] children = node.getChildren();
		for ( int i = 0; i < children.length; ++i ) {

			INode child = children[i];
			graphics.setStyle(this.getStyle(child).getBranchStyle());
			graphics.drawRightAngle(layout.getPosition(node), layout.getPosition(child));
			
			renderNode(child, layout, graphics, renderCallback);
		}
	}
}
