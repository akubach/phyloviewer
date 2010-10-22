package org.iplantc.phyloviewer.shared.math;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AnnularSector implements IsSerializable {
	
	private PolarVector2 min;
	private PolarVector2 max;
	
	public AnnularSector() {
		this.min = new PolarVector2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		this.max = new PolarVector2(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
	}
	
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
	
	public void expandBy(AnnularSector other) {
		if (!other.isValid()) {
			return;
		}
		
		double minRadius = Math.min(other.getMin().getRadius(), this.getMin().getRadius());
		double maxRadius = Math.max(other.getMax().getRadius(), this.getMax().getRadius());
		double minAngle = Math.min(other.getMin().getAngle(), this.getMin().getAngle());
		double maxAngle = Math.max(other.getMax().getAngle(), this.getMax().getAngle());
		
		min.setRadius(minRadius);
		min.setAngle(minAngle);
		max.setRadius(maxRadius);
		max.setAngle(maxAngle);
	}

	public void expandBy(Vector2 v) {
		PolarVector2 pv = new PolarVector2(v);
		this.expandBy(pv);
	}
	
	public void expandBy(PolarVector2 pv) {
		if (!pv.isValid()) {
			return;
		}
		
		min.setAngle(Math.min(min.getAngle(), pv.getAngle()));
		max.setAngle(Math.max(max.getAngle(), pv.getAngle()));
		min.setRadius(Math.min(min.getRadius(), pv.getRadius()));
		max.setRadius(Math.max(max.getRadius(), pv.getRadius()));
	}

	public boolean contains(Vector2 position) {
		PolarVector2 pv = new PolarVector2(position);
		return this.contains(pv);
	}
	
	public boolean contains(PolarVector2 pv) {
		return pv.isValid() && this.containsRadius(pv.getRadius()) && this.containsAngle(pv.getAngle());
	}
	
	public boolean intersects(AnnularSector other) {
		return Math.max ( min.getRadius(), other.getMin().getRadius() ) <= Math.min ( max.getRadius(), other.getMax().getRadius() ) &&
        Math.max ( min.getAngle(), other.getMin().getAngle() ) <= Math.min ( max.getAngle(), other.getMax().getAngle() );
	}

	public boolean intersects(Box2D box,Vector2 center) {
		//this is a rough estimate.  Probably good enough for culling or picking purposes 
		return cartesianBounds(center).intersects(box);
	}
	
	public Box2D cartesianBounds(Vector2 center) {
		
		Vector2 min = this.getMin().toCartesian(center);
		Vector2 otherInsideCorner = new PolarVector2(this.getMin().getRadius(), this.getMax().getAngle()).toCartesian(center);
		
		Box2D bounds = getOuterArcBounds(center);
		bounds.expandBy(min);
		bounds.expandBy(otherInsideCorner);
		
		return bounds;
	}
	
	public boolean isValid() {
		return min.isValid() && max.isValid() 
			&& max.getRadius() >= min.getRadius()
			&& max.getAngle() >= min.getAngle();
	}
	
	/** 
	 * Should be tight cartesian bounds of the outside arc of this AnnularSector
	 */
	public Box2D getOuterArcBounds(Vector2 center) {
		double[] anglesToCheck = {0.0, Math.PI/2, Math.PI, 3 * Math.PI/2, 2 * Math.PI, this.min.getAngle(), this.max.getAngle()};
		double outerRadius = this.getMax().getRadius();
		
		Box2D bounds = new Box2D();
		for (double angle : anglesToCheck) {
			if (this.containsAngle(angle)) {
				bounds.expandBy(new PolarVector2(outerRadius, angle).toCartesian(center));
			}
		}
		
		return bounds;
	}
	
	private boolean containsAngle(double angle) {
		return angle >= min.getAngle() && angle <= max.getAngle();
	}
	
	private boolean containsRadius(double radius) {
		return radius >= min.getRadius() && radius <= max.getRadius();
	}

	@Override
	public String toString() {
		return "[" + this.min + ", " + this.max + "]"; 
	}
}
