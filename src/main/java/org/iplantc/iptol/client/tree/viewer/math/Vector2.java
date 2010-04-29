package org.iplantc.iptol.client.tree.viewer.math;

public class Vector2 {

	private double _x = 0.0;
	private double _y = 0.0;
	
	public Vector2() {
	}
	
	public Vector2(double x, double y) {
		_x = x;
		_y = y;
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
}
