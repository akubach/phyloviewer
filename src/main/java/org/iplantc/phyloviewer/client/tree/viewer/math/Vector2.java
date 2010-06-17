/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.math;

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

	public Vector2 substract(Vector2 nodePosition) {
		return new Vector2 ( this.getX() - nodePosition.getX(), this.getY() - nodePosition.getY() );
	}

	public double length() {
		return Math.sqrt((this.getX() * this.getX()) + (this.getY() * this.getY()));
	}
}
