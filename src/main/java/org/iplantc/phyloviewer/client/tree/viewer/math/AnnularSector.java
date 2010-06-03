package org.iplantc.phyloviewer.client.tree.viewer.math;

import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

public class AnnularSector {
	private PolarVector2 min;
	private PolarVector2 max;
	
	public AnnularSector(PolarVector2 min, PolarVector2 max) {
		if (min == max) {
			max = new PolarVector2(min);
		}
		this.min = min;
		this.max = max;
	}
	
	public AnnularSector(PolarVector2 initPoint) {
		this(initPoint, new PolarVector2(initPoint));
	}
	
	public PolarVector2 getMin() {
		return min;
	}

	public void setMin(PolarVector2 min) {
		this.min = min;
	}

	public PolarVector2 getMax() {
		return max;
	}

	public void setMax(PolarVector2 max) {
		this.max = max;
	}
	
	public void expandBy(AnnularSector bb) {
		//TODO
	}

	public void expandBy(Vector2 v) {
		PolarVector2 pv;
		if (v instanceof PolarVector2) {
			pv = (PolarVector2) v;
		} else {
			pv = new PolarVector2(v);
		}
		
		if (!pv.isValid()) {
			return;
		}
		
		min.setAngle(Math.min(min.getAngle(), pv.getAngle()));
		max.setAngle(Math.max(max.getAngle(), pv.getAngle()));
		min.setRadius(Math.min(min.getRadius(), pv.getRadius()));
		max.setRadius(Math.max(max.getRadius(), pv.getRadius()));
	}

	public boolean contains(Vector2 position) {
		PolarVector2 pv;
		if (position instanceof PolarVector2) {
			pv = (PolarVector2) position;
		} else {
			pv = new PolarVector2(position);
		}
		
		return pv.isValid()
			&& pv.getAngle() >= min.getAngle() && pv.getRadius() >= min.getRadius()
			&& pv.getAngle() <= max.getAngle() && pv.getRadius() <= max.getRadius();
	}
	
	public boolean intersects(AnnularSector other) {
		return Math.max ( min.getRadius(), other.getMin().getRadius() ) <= Math.min ( max.getRadius(), other.getMax().getRadius() ) &&
        Math.max ( min.getY(), other.getMin().getY() ) <= Math.min ( max.getY(), other.getMax().getY() );
	}

	public boolean intersects(Box2D box) {
		//this is a rough estimate.  Probably good enough for culling or picking purposes 
		return cartesianBounds().intersects(box);
	}
	
	public Box2D cartesianBounds() {
		Arc2D outerArc = new Arc2D.Double();
		outerArc.setArcByCenter(0, 0, max.getRadius(), min.getAngle(), max.getAngle(),  Arc2D.OPEN);
		Rectangle2D arcBounds = outerArc.getBounds2D();
		
		PolarVector2 otherInsideCorner = new PolarVector2(min);
		otherInsideCorner.setAngle(max.getAngle());
		
		Box2D bounds = new Box2D(new Vector2(arcBounds.getMinX(), arcBounds.getMinY()), new Vector2(arcBounds.getMaxX(), arcBounds.getMaxY()));
		bounds.expandBy(min);
		bounds.expandBy(otherInsideCorner);
		
		return bounds;
	}
	
	public boolean isValid() {
		return min.isValid() && max.isValid() 
			&& max.getRadius() > min.getRadius()
			&& max.getAngle() > min.getAngle();
	}
}
