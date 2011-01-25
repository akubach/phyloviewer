package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.scene.BranchBuilderCladogram;
import org.iplantc.phyloviewer.shared.scene.Drawable;


public class RenderTreeCladogram extends RenderTree {

	BranchBuilderCladogram branchBuilder = new BranchBuilderCladogram();
	
	public RenderTreeCladogram() {
	}
	
	public RenderTreeCladogram(IDocument document) {
		super(document);
	}
	
	protected void drawLabel(INode node, ILayoutData layout, IGraphics graphics) {
		Vector2 position = layout.getPosition(node);
		this.drawLabel(node, graphics, position);
	}

	protected void renderPlaceholder(INode node, ILayoutData layout,
			IGraphics graphics) {
		Box2D boundingBox = layout.getBoundingBox(node);
		Vector2 min = boundingBox.getMin();
		Vector2 max = boundingBox.getMax();

		graphics.setStyle(this.getStyle(node).getGlyphStyle());
		drawTriangle(graphics,layout.getPosition(node),max.getX(),min.getY(),max.getY());

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

	protected void renderChildren(INode node, ILayoutData layout, IGraphics graphics) 
	{
		INode[] children = node.getChildren();
		for ( int i = 0; i < children.length; ++i )
		{
			INode child = children[i];
			Drawable[] drawables = branchBuilder.buildBranch(node, child, layout);
			graphics.setStyle(this.getStyle(child).getBranchStyle());
			for(Drawable drawable : drawables)
			{
				drawable.draw(graphics);
			}

			renderNode(child, layout, graphics);
		}
	}
	
	private static void drawTriangle(IGraphics graphics, Vector2 v0,double x, double y0, double y1){
		Vector2 v1 = new Vector2(x,y0);
		Vector2 v2 = new Vector2(x,y1);
		
		Vector2 vertices[] = new Vector2[3];
		vertices[0] = v0;
		vertices[1] = v1;
		vertices[2] = v2;
		graphics.drawPolygon(vertices);
	}
}
