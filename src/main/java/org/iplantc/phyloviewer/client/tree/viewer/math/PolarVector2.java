package org.iplantc.phyloviewer.client.tree.viewer.math;

public class PolarVector2 extends Vector2 {

	private double radius = 0.0;
	private double angle = 0.0;
	
	public PolarVector2() {
	}
	
	/**
	 * note: this constructor allows vectors that will be !isValid()
	 */
	public PolarVector2(double radius, double angle) {
		this.radius = radius;
		this.angle = angle;
	}
	
	public PolarVector2(PolarVector2 toCopy) {
		this.radius = toCopy.getRadius();
		this.angle = toCopy.getAngle();
	}
	
	public PolarVector2(Vector2 v) {
		radius = Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
		setAngle(v.getX(), v.getY());
	}	

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		//TODO if for some reason radius is negative, make it positive and add PI to angle to keep this.isValid()?
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle % (2 * Math.PI);
	}

	@Override
	public double getX() {
		return radius * Math.cos(angle);
	}

	@Override
	public double getY() {
		return radius * Math.sin(angle);
	}

	@Override
	public void setX(double x) {
		double y = this.getY();
		radius = Math.sqrt(x * x + y * y);
		setAngle(x, y);
	}

	@Override
	public void setY(double y) {
		double x = this.getX();
		radius = Math.sqrt(x * x + y * y);
		setAngle(x, y);
	}

	@Override
	public double length() {
		return radius;
	}
	
	public boolean isValid() {
		return radius >= 0 && angle >= 0 && angle < 2 * Math.PI;
	}
	
	/** 
	 * Sets the angle for this PolarVector2 to a value in the range [0,2*pi) 
	 * for the given cartesian point (x,y)
	 */
	private void setAngle(double x, double y) {
		angle = Math.atan2(y, x);
		angle += 2 * Math.PI; //range of atan2 is -pi to pi
		angle %= (2 * Math.PI);
	}
	
	@Override
	public String toString() {
		return "(" + this.radius + ", " + this.angle + ")";
	}
}
