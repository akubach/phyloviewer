/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.math;

public class Box2D {
	
	private Vector2 min = new Vector2(Double.MAX_VALUE,Double.MAX_VALUE);
	private Vector2 max = new Vector2(Double.MIN_VALUE,Double.MIN_VALUE);
	
	public Box2D() {
	}
	
	public Boolean valid() {
	  return max.getX() >= min.getX() && max.getY() >= min.getY();
	}

	public Box2D(Vector2 min, Vector2 max) {
		this.min = min;
		this.max = max;
	}

	public Vector2 getMin() {
		return min;
	}

	public void setMin(Vector2 min) {
		this.min = min;
	}

	public Vector2 getMax() {
		return max;
	}

	public void setMax(Vector2 max) {
		this.max = max;
	}
	
	public void expandBy ( Vector2 v ) {
		if ( v.getX() < min.getX() ) min.setX( v.getX() );
	    if ( v.getX() > max.getX() ) max.setX( v.getX() );
	
	    if ( v.getY() < min.getY() ) min.setY ( v.getY() );
	    if ( v.getY() > max.getY() ) max.setY ( v.getY() );
	}
	
	public void expandBy ( Box2D bb ) {
	    if ( false == bb.valid() ) return;

	    if ( bb.getMin().getX() < min.getX() ) min.setX( bb.getMin().getX() );
	    if ( bb.getMax().getX() > max.getX() ) max.setX( bb.getMax().getX() );

	    if ( bb.getMin().getY() < min.getY() ) min.setY ( bb.getMin().getY() );
	    if ( bb.getMax().getY() > max.getY() ) max.setY ( bb.getMax().getY() );
	}
	
	// Return true if the given box intersects this one.
	public boolean intersects ( Box2D bb ) {
		return Math.max ( min.getX(), bb.getMin().getX() ) <= Math.min ( max.getX(), bb.getMax().getX() ) &&
	           Math.max ( min.getY(), bb.getMin().getY() ) <= Math.min ( max.getY(), bb.getMax().getY() );
	}

	public boolean contains(Vector2 position) {
		return this.valid() && 
			( position.getX() >= this.getMin().getX() && position.getX() <= this.getMax().getX() &&
			  position.getY() >= this.getMin().getY() && position.getY() <= this.getMax().getY() );
	}

	public Vector2 getCenter() {
		double x = ( min.getX() + max.getX() ) / 2.0;
	    double y = ( min.getY() + max.getY() ) / 2.0;
	    return new Vector2 ( x, y );
	}
	
	public double getWidth() {
		return max.getX() - min.getX();
	}
	
	public double getHeight() {
		return max.getY() - min.getY();
	}
	
	public String toString() {
		return "[" + min.toString() + ", " + max.toString() + "]";
	}
}
