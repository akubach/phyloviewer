package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;

public class Rectangle extends Polygon
{
	public Rectangle()
	{
		super(new Vector2[] {new Vector2(), new Vector2(), new Vector2(), new Vector2()});
	}
	
	public Rectangle(Vector2 min, Vector2 max)
	{
		this();
		setMin(min);
		setMax(max);
	}
	
	public Rectangle(Box2D box)
	{
		this(box.getMin(), box.getMax());
	}
	
	public Vector2 getMax()
	{
		return vertices[2];
	}

	public Vector2 getMin()
	{
		return vertices[0];
	}

	public void setMax(Vector2 max)
	{
		//vertices[0] is min
		vertices[1] = new Vector2(max.getX(), getMin().getY());
		vertices[2] = max;
		vertices[3] = new Vector2(getMin().getX(), max.getY());
	}

	public void setMin(Vector2 min)
	{
		vertices[0] = min;
		vertices[1] = new Vector2(getMax().getX(), min.getY());
		//vertices[2] is max
		vertices[3] = new Vector2(min.getX(), getMax().getY());
	}

	@Override
	public int getDrawableType()
	{
		return TYPE_POLYGON;
	}
}
