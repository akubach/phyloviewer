package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;

public abstract class Drawable {
	
	Box2D boundingBox = new Box2D();

	public Drawable() {
	}
	
	public Box2D getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(Box2D boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public boolean intersect(Vector2 position,double distanceSquared)
	{
		return false;
	}

	public abstract void draw(IGraphics graphics);
}
