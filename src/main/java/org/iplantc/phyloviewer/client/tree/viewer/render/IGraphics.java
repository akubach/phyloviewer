package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;

public interface IGraphics {

	public abstract void clear();

	public abstract void drawPoint(Vector2 position);
	
	public abstract void drawLine(Vector2 start, Vector2 end);

	public abstract void drawRightAngle(Vector2 start, Vector2 end);

	public abstract void drawText(Vector2 position, String text);

	public abstract void drawTriangle(Vector2 v0, double x, double y0, double y1);

	public abstract void setViewMatrix(Matrix33 matrix);

	public abstract Boolean isCulled(Box2D iBox2D);

}