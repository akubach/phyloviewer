/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.math;

public class Vector3 {

	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;
	
	public Vector3() {
	}
	
	public Vector3(double x, double y,double z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public Vector3 cross(Vector3 v) {
		return new Vector3 ( 
			      this.y * v.z - this.z * v.y,
			      this.z * v.x - this.x * v.z,
			      this.x * v.y - this.y * v.x );
	}
	
	public double dot(Vector3 v) {
		return 
	      this.x * v.x +
	      this.y * v.y +
	      this.z * v.z;
	}
}
