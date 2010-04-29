package org.iplantc.de.client.tree.viewer.math;

public class Vector3 {

	private double _x = 0.0;
	private double _y = 0.0;
	private double _z = 0.0;
	
	public Vector3() {
	}
	
	public Vector3(double x, double y,double z) {
		_x = x;
		_y = y;
		_z = z;
	}

	public double getX() {
		return _x;
	}

	public void setX(double x) {
		_x = x;
	}

	public double getY() {
		return _y;
	}

	public void setY(double y) {
		_y = y;
	}
	
	public double getZ() {
		return _z;
	}

	public void setZ(double z) {
		_z = z;
	}
	
	public Vector3 cross(Vector3 v) {
		return new Vector3 ( 
			      this._y * v._z - this._z * v._y,
			      this._z * v._x - this._x * v._z,
			      this._x * v._y - this._y * v._x );
	}
	
	public double dot(Vector3 v) {
		return 
	      this._x * v._x +
	      this._y * v._y +
	      this._z * v._z;
	}
}
