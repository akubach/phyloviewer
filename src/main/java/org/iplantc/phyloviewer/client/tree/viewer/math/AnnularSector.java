package org.iplantc.phyloviewer.client.tree.viewer.math;

public class AnnularSector {
	private PolarVector2 min = new PolarVector2(Double.MAX_VALUE,Double.MAX_VALUE);
	private PolarVector2 max = new PolarVector2(Double.MIN_VALUE,Double.MIN_VALUE);
	
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
		// TODO if v is not instanceof PolarVector2 do a conversion first, then expand
		throw new RuntimeException("Not yet implemented");
	}

	public boolean contains(Vector2 position) {
		// TODO if v is not instanceof PolarVector2 do a conversion first, then check contains
		throw new RuntimeException("Not yet implemented");
	}
	
	public boolean intersects(AnnularSector other) {
		//TODO
		throw new RuntimeException("Not yet implemented");
	}

	public boolean intersects(Box2D box) {
		//this is a rough estimate.  Probably good enough for culling or picking purposes 
		return cartesianBounds().intersects(box);
	}
	
	public Box2D cartesianBounds() {
		//TODO return the (cartesian) Box2D containing this sector. 
		throw new RuntimeException("Not yet implemented");
	}
}
