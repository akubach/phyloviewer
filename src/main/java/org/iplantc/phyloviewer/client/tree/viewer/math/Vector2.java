/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.math;

public class Vector2 {

	private double x = 0.0;
	private double y = 0.0;
	
	public Vector2() {
	}
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Vector2 subtract(Vector2 nodePosition) {
		return new Vector2 ( this.getX() - nodePosition.getX(), this.getY() - nodePosition.getY() );
	}

	public double length() {
		return Math.sqrt((this.getX() * this.getX()) + (this.getY() * this.getY()));
	}
}
