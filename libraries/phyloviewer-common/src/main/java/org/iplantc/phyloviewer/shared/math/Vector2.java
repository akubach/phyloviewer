/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.math;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Vector2 implements IsSerializable
{
	private double x = 0.0;
	private double y = 0.0;

	public Vector2()
	{
	}

	public Vector2(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vector2 clone()
	{
		return new Vector2(x, y);
	}

	/**
	 * Get the x component
	 * 
	 * @return The value of the x component.
	 */
	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	/**
	 * Get the y component
	 * 
	 * @return The value of the y component.
	 */
	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public Vector2 add(Vector2 rhs)
	{
		return new Vector2(this.getX() + rhs.getX(), this.getY() + rhs.getY());
	}

	public Vector2 subtract(Vector2 nodePosition)
	{
		return new Vector2(this.getX() - nodePosition.getX(), this.getY() - nodePosition.getY());
	}

	public double length()
	{
		return Math.sqrt((this.getX() * this.getX()) + (this.getY() * this.getY()));
	}

	public Vector2 rotate(double angle)
	{
		double x = this.getX() * Math.cos(angle) - this.getY() * Math.sin(angle);
		double y = this.getX() * Math.sin(angle) + this.getY() * Math.cos(angle);
		return new Vector2(x, y);
	}

	public double distance(Vector2 other)
	{
		return Math.sqrt(this.distanceSquared(other));
	}

	public double distanceSquared(Vector2 other)
	{
		return ((this.getX() - other.getX()) * (this.getX() - other.getX()))
				+ ((this.getY() - other.getY()) * (this.getY() - other.getY()));
	}

	public String toString()
	{
		return "(" + x + "," + y + ")";
	}

	public String toJSON()
	{
		return "{\"x\":" + this.getX() + ",\"y\":" + this.getY() + "}";
	}
}
