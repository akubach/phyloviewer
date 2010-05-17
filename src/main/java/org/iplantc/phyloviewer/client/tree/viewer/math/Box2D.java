package org.iplantc.phyloviewer.client.tree.viewer.math;

public class Box2D {
	private Vector2 _min = new Vector2(Double.MAX_VALUE,Double.MAX_VALUE);
	private Vector2 _max = new Vector2(Double.MIN_VALUE,Double.MIN_VALUE);
	
	public Box2D() {
	}
	
	public Boolean valid() {
	  return _max.getX() >= _min.getX() && _max.getY() >= _min.getY();
	}

	public Box2D(Vector2 min, Vector2 max) {
		_min = min;
		_max = max;
	}

	public Vector2 getMin() {
		return _min;
	}

	public void setMin(Vector2 min) {
		_min = min;
	}

	public Vector2 getMax() {
		return _max;
	}

	public void setMax(Vector2 max) {
		_max = max;
	}
	
	public void expandBy ( Vector2 v ) {
		if ( v.getX() < _min.getX() ) _min.setX( v.getX() );
	    if ( v.getX() > _max.getX() ) _max.setX( v.getX() );
	
	    if ( v.getY() < _min.getY() ) _min.setY ( v.getY() );
	    if ( v.getY() > _max.getY() ) _max.setY ( v.getY() );
	}
	
	public void expandBy ( Box2D bb ) {
	    if ( false == bb.valid() ) return;

	    if ( bb._min.getX() < _min.getX() ) _min.setX( bb._min.getX() );
	    if ( bb._max.getX() > _max.getX() ) _max.setX( bb._max.getX() );

	    if ( bb._min.getY() < _min.getY() ) _min.setY ( bb._min.getY() );
	    if ( bb._max.getY() > _max.getY() ) _max.setY ( bb._max.getY() );
	}
	
	// Return true if the given box intersects this one.
	public boolean intersects ( Box2D bb ) {
		return Math.max ( _min.getX(), bb._min.getX() ) <= Math.min ( _max.getX(), bb._max.getX() ) &&
	           Math.max ( _min.getY(), bb._min.getY() ) <= Math.min ( _max.getY(), bb._max.getY() );
	}

	public boolean contains(Vector2 position) {
		return this.valid() && 
			( position.getX() >= this.getMin().getX() && position.getX() <= this.getMax().getX() &&
			  position.getY() >= this.getMin().getY() && position.getY() <= this.getMax().getY() );
	}

	public Vector2 getCenter() {
		double x = ( _min.getX() + _max.getX() ) / 2.0;
	    double y = ( _min.getY() + _max.getY() ) / 2.0;
	    return new Vector2 ( x, y );
	}
}
