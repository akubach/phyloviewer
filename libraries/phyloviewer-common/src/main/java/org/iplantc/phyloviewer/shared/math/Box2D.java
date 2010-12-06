/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.math;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Box2D implements IsSerializable {
	
	private Vector2 min = new Vector2(Double.MAX_VALUE,Double.MAX_VALUE);
	private Vector2 max = new Vector2(Double.MIN_VALUE,Double.MIN_VALUE);
	
	public Box2D() {
	}
	
	public Boolean valid() {
	  return max.getX() >= min.getX() && max.getY() >= min.getY();
	}

	public Box2D(Vector2 min, Vector2 max) {
		setMin(min);
		setMax(max);
	}

	public Vector2 getMin() {
		return min;
	}

	public void setMin(Vector2 min) {
		if (min == this.max)
		{
			//we don't want these to be the same object, or mutators with change both at once
			min = new Vector2(min.getX(), min.getY());
		}
		
		this.min = min;
	}

	public Vector2 getMax() {
		return max;
	}

	public void setMax(Vector2 max) {
		if (max == this.min)
		{
			//we don't want these to be the same object, or mutators with change both at once
			max = new Vector2(max.getX(), max.getY());
		}
		
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
	
	public void expandBy(double d)
	{
		min.setX(min.getX() - d);
		min.setY(min.getY() - d);
		max.setX(max.getX() + d);
		max.setY(max.getY() + d);
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
	
	/**
	 * @return the box defined by the given corners (any two opposite corners of the box)
	 */
	public static Box2D createBox(Vector2 v0, Vector2 v1)
	{
		Box2D box = new Box2D(v0, v0);
		box.expandBy(v1);
		return box;
	}
}
