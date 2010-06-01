package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

/**
 * Given a tree with a cartesian layout, renders it in a circle
 */
public class RenderTreeCircular {
	private static final double labelMargin = 0.1;
	private static final double WIDTH = 1.0;
	
	public static void renderTree(ITree tree, IGraphics graphics, Camera camera) {
		if ( tree == null || graphics == null )
			return;
		
		INode root = tree.getRootNode();
		
		if ( root == null )
			return;
		
		if (camera!=null){
			graphics.setViewMatrix(camera.getMatrix());
		}
		
		graphics.clear();
		
		Box2D layoutBounds = root.getBoundingBox();
		
		renderNode(root,graphics,camera, layoutBounds);
	}

	private static void renderNode(INode node, IGraphics graphics, Camera camera, Box2D layoutBounds) {
		Box2D polarBox = scaleToPolar(node.getBoundingBox(), layoutBounds);
		Vector2 transformPosition = transformPosition(node.getPosition(), layoutBounds);

		if ( graphics.isCulled(rectBounds(polarBox)))
			return;
		
		if (canDrawLeafLabels(node.getNumberOfLeafNodes(), polarBox, camera)) {
			graphics.drawPoint(transformPosition);
			renderChildren(node, graphics, camera, layoutBounds);
		} else {
			renderPlaceholder(node);
		}
		
		if (node.isLeaf()) {
			drawLabel(node.getLabel(), transformPosition, graphics);
		}
		
	}
	
	private static Vector2 transformPosition(Vector2 point, Box2D layoutBounds) {
		Vector2 polar = scaleToPolar(point, layoutBounds);
		return polarToCartesian(polar);
	}

	/** 
	 * Rescales a Box2D from a layout in cartesian coordinates to the range [0,1], [0,2*PI].
	 * For using a cartesian layout as the basis of a circular layout.
	 * @return a new Box2D in polar coordinates. (Really a annular sector, not a box, so some Box2D methods are invalidated.)
	 */
	private static Box2D scaleToPolar(Box2D rectBox, Box2D layoutBounds) {
		Vector2 min = scaleToPolar(rectBox.getMin(), layoutBounds);
		Vector2 max = scaleToPolar(rectBox.getMax(), layoutBounds);
		
		return new Box2D(min, max);
	}
	
	/** 
	 * Rescales a node position from a layout cartesian coordinates to the range [0,1], [0,2*PI] 	 
	 * For using a cartesian layout as the basis of a circular layout.
	 * @return a new Vector2 in polar coordinates
	 */
	private static Vector2 scaleToPolar(Vector2 point, Box2D layoutBounds) {
		double maxRadius = 0.5 - labelMargin;
		double r = (point.getX() / layoutBounds.getMax().getX()) * maxRadius;
		double a = 2 * Math.PI * (point.getY() / layoutBounds.getMax().getY());
		
		return new Vector2(r, a);
	}
	
	private static Vector2 polarToCartesian(Vector2 point) {
		double r = point.getX();
		double a = point.getY();
		return new Vector2(r * Math.cos(a) + WIDTH / 2, r * Math.sin(a) + WIDTH / 2);
	}
	
	/**
	 * @return the cartesian bounds of a (polar) annular sector.
	 */
	private static Box2D rectBounds(Box2D polarBox) {
		//TODO
		throw new RuntimeException("Not yet implemented");
	}
	
	private static void drawLabel(String label, Vector2 position, IGraphics graphics) {
		//TODO rotate so labels are along radii
		
		graphics.drawText(position, label);
	}
	
	private static boolean canDrawLeafLabels(int numberOfLeaves, Box2D polarBounds, Camera camera) {
		int pixelsPerTaxon = 15;
		double pixelsNeeded = numberOfLeaves * pixelsPerTaxon;
		
		double arcLength = (polarBounds.getMax().getY() - polarBounds.getMin().getY()) * polarBounds.getMax().getX();
		//TODO find out how many pixels of arc this is
		
		System.err.println("Collapsing subtrees not yet implemented");
		return true;
	}
	
	private static void renderPlaceholder(INode node) {
		// TODO 
		throw new RuntimeException("Not yet implemented");
	}
	
	private static void renderChildren(INode node, IGraphics graphics, Camera camera, Box2D layoutBounds) {
		int numChildren = node.getNumberOfChildren();
		for ( int i = 0; i < numChildren; ++i ) {
			renderBranch(node, node.getChild(i), graphics, layoutBounds);
			
			renderNode(node.getChild(i),graphics,camera, layoutBounds);
		}
	}
	
	private static void renderBranch(INode node, INode child, IGraphics graphics, Box2D layoutBounds) {
		//A straight line for now.  TODO: Draw the usual arcs and radii
		Vector2 parentPosition = transformPosition(node.getPosition(), layoutBounds);
		Vector2 childPosition = transformPosition(child.getPosition(), layoutBounds);
		graphics.drawLine(parentPosition, childPosition);
	}
}
