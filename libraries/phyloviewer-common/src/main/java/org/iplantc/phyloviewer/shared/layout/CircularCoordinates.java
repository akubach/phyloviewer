package org.iplantc.phyloviewer.shared.layout;

import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

public class CircularCoordinates
{
	// These parameters will place the nodes in a unit square at (0,0)
	final static double RADIUS = 0.5;
	final static Vector2 CENTER = new Vector2(0.5, 0.5);

	/**
	 * Get the polar position for the node.
	 * @param node
	 * @param layout
	 * @return
	 */
	public static PolarVector2 getPolarPosition(INode node, ILayoutData layout)
	{
		return convertToPolar(layout.getPosition(node));
	}

	/**
	 * Get the polar bounding box for the node.
	 * @param node
	 * @param layout
	 * @return
	 */
	public static AnnularSector getPolarBoundingBox(INode node, ILayoutData layout)
	{
		Box2D bbox = layout.getBoundingBox(node);
		return new AnnularSector(convertToPolar(bbox.getMin()), convertToPolar(bbox.getMax()));
	}

	/**
	 * Convert the vector from layout into polar coordinates.
	 * @param vector
	 * @return
	 */
	public static PolarVector2 convertToPolar(Vector2 vector)
	{
		double r = RADIUS * (vector.getX() / 0.8);
		double angle = (2 * Math.PI) * vector.getY();
		return new PolarVector2(r, Math.min(angle, 2 * Math.PI));
	}
	
	/**
	 * Convert from polar to Cartesian.
	 * @param polarVector
	 * @return
	 */
	public static Vector2 convertToCartesian(PolarVector2 polarVector)
	{
		return polarVector.toCartesian(CENTER);
	}
	
	
	/**
	 * 
	 * @param node
	 * @param layout
	 * @return
	 */
	public static Vector2 getCartesianPosition(INode node, ILayoutData layout)
	{
		return convertToCartesian(getPolarPosition(node,layout));
	}
	
	/**
	 * Get the bounding box in Cartesian coordinates.
	 * @param node
	 * @param layout
	 * @return
	 */
	public static Box2D getCartesianBoundingBox(INode node, ILayoutData layout)
	{
		Box2D bounds = getPolarBoundingBox(node, layout).cartesianBounds();

		Vector2 min = bounds.getMin().add(CENTER);
		Vector2 max = bounds.getMax().add(CENTER);
		return new Box2D(min, max);
	}

	/**
	 * Convert the bounding box to Cartesian.
	 * @param box
	 * @return
	 */
	public static Box2D convertBoundingBox(Box2D box)
	{
		AnnularSector polar = new AnnularSector(convertToPolar(box.getMin()),
				convertToPolar(box.getMax()));
		Box2D bounds = polar.cartesianBounds();

		Vector2 min = bounds.getMin().add(CENTER);
		Vector2 max = bounds.getMax().add(CENTER);
		return new Box2D(min, max);
	}
	
	public static Vector2 getCenter()
	{
		return CENTER;
	}
}
