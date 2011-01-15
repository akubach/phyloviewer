/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.math;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Vector2 implements IsSerializable {

	private double x = 0.0;
	private double y = 0.0;
	
	public Vector2() {
	}
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.math.IVector2#getX()
	 */
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.math.IVector2#getY()
	 */
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public Vector2 add(Vector2 rhs) {
		return new Vector2(this.getX() + rhs.getX(),this.getY() + rhs.getY());
	}

	public Vector2 subtract(Vector2 nodePosition) {
		return new Vector2 ( this.getX() - nodePosition.getX(), this.getY() - nodePosition.getY() );
	}

	public double length() {
		return Math.sqrt((this.getX() * this.getX()) + (this.getY() * this.getY()));
	}
	
	public Vector2 rotate(double angle) {
		double x = this.getX() * Math.cos(angle) - this.getY() * Math.sin(angle);
		double y = this.getX() * Math.sin(angle) + this.getY() * Math.cos(angle);
		return new Vector2(x,y);
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	public String toJSON() {
		return "{\"x\":" + this.getX() + ",\"y\":" + this.getY() + "}";
	}
}
