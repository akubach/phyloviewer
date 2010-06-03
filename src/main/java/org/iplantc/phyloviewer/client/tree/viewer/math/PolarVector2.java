package org.iplantc.phyloviewer.client.tree.viewer.math;

public class PolarVector2 extends Vector2 {

	private double radius = 0.0;
	private double angle = 0.0;
	
	public PolarVector2() {
	}
	
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
		angle = Math.atan2(v.getY(), v.getX()) + Math.PI;
	}	

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
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
		angle = Math.atan2(y, x) + Math.PI;
	}

	@Override
	public void setY(double y) {
		double x = this.getX();
		radius = Math.sqrt(x * x + y * y);
		angle = Math.atan2(y, x) + Math.PI;
	}

	@Override
	public double length() {
		return radius;
	}
}
