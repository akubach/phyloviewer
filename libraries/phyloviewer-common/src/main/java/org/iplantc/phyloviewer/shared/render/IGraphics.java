/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;

public interface IGraphics {

	public abstract void clear();

	public abstract void drawPoint(Vector2 position);
	
	public abstract void drawLine(Vector2 start, Vector2 end);

	public abstract void drawRightAngle(Vector2 start, Vector2 end);

	public abstract void drawText(Vector2 position, Vector2 offset, String text);
	
	public abstract void drawTextRadial(PolarVector2 position, String text);

	public abstract void drawTriangle(Vector2 v0, double x, double y0, double y1);
	
	public abstract void drawWedge(Vector2 peak, PolarVector2 base0, PolarVector2 base1);

	public abstract void setViewMatrix(Matrix33 matrix);
	
	public abstract Matrix33 getViewMatrix();

	public abstract Boolean isCulled(Box2D iBox2D);
	
	public abstract void drawArc(Vector2 center, double radius, double startAngle, double endAngle);
	
	/**
	 * Sets the drawing style.  Any element style fields that are null or NaN will leave the previous style in place for that field.
	 * @param style
	 */
	public abstract void setStyle(IBranchStyle style);
	public abstract void setStyle(IGlyphStyle style);
	public abstract void setStyle(ILabelStyle style);
	public abstract void setStyle(INodeStyle style);

	public abstract Box2D getDisplayedBox(Box2D box);
}