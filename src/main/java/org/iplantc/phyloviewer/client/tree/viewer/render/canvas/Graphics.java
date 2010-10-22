/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render.canvas;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.render.Defaults;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.IElementStyle;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;


public class Graphics implements IGraphics {

	private static final double DEGREES_270 = 3 * Math.PI / 2;
	private static final double DEGREES_90 = Math.PI / 2;
	private Canvas canvas = null;
	private Matrix33 matrix = new Matrix33();
	private Box2D screenBounds = new Box2D();
	private List<Box2D> drawnTextExtents = new ArrayList<Box2D>();
	
	public Graphics(Canvas canvas) {
		this.canvas = canvas;
		screenBounds.setMin(new Vector2(0,0));
		screenBounds.setMax(new Vector2(canvas.getWidth(),canvas.getHeight()));
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#clear()
	 */
	public void clear() {
		drawnTextExtents.clear();
		canvas.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#drawPoint(org.iplantc.phyloviewer.client.tree.viewer.math.Vector2)
	 */
	public void drawPoint(Vector2 position) {
		Vector2 p = matrix.transform(position);
		
		canvas.beginPath();
		canvas.arc(p.getX(), p.getY(), Defaults.POINT_RADIUS, 0, Math.PI*2, true); 
		canvas.closePath();
		canvas.fill();
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#drawLine(org.iplantc.phyloviewer.client.tree.viewer.math.Vector2, org.iplantc.phyloviewer.client.tree.viewer.math.Vector2)
	 */
	public void drawRightAngle(Vector2 start, Vector2 end) {
		Vector2 p0 = matrix.transform(start);
		Vector2 p1 = matrix.transform(end);
		
		canvas.beginPath();
		canvas.moveTo(p0.getX(),p0.getY());
		canvas.lineTo(p0.getX(),p1.getY());
		canvas.lineTo(p1.getX(),p1.getY());
		canvas.stroke();
	}

	@Override
	public void drawLine(Vector2 start, Vector2 end) {
		Vector2 p0 = matrix.transform(start);
		Vector2 p1 = matrix.transform(end);
		
		canvas.beginPath();
		canvas.moveTo(p0.getX(),p0.getY());
		canvas.lineTo(p1.getX(),p1.getY());
		canvas.stroke();
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#drawText(org.iplantc.phyloviewer.client.tree.viewer.math.Vector2, java.lang.String)
	 */
	public void drawText(Vector2 position, Vector2 offset, String text) {
		this.drawText(position,offset,text,0.0);
	}
	
	public void drawText(Vector2 position, Vector2 offset, String text, double angle) {
		Vector2 p = matrix.transform(position);
		
		Vector2 startingPosition = new Vector2 ( p.getX() + offset.getX(), p.getY() + offset.getY() );
		
		// TODO: Get the text height from the canvas.
		float height = 10;
		double width = canvas.measureText(text);
		
		// Make a bounding box of the text.
		Box2D bbox = createBoundingBox(startingPosition, height, width,angle);
		
		// If this bounding box will intersect any text that we have already drawn, don't draw.
		// If this brute force search proves to be too slow, perhaps a quad tree search would be better.
		for ( Box2D box : drawnTextExtents ) {
			if ( box.intersects(bbox)) {
				return;
			}
		}
		
		canvas.save();
		canvas.translate(startingPosition.getX(), startingPosition.getY());
		canvas.rotate(angle);
		
		if ( angle > DEGREES_90 && angle < DEGREES_270) {
			//flip labels on the left side of the circle so they are right-side-up
			canvas.translate(width, -height);
			canvas.rotate(Math.PI);
		}
		
		canvas.fillText(text, 0.0, 0.0);
		drawnTextExtents.add(bbox);
		
		canvas.restore();
	}

	private Box2D createBoundingBox(Vector2 startingPosition, float height,
			double width, double angle) {
		Vector2 min = new Vector2 ( startingPosition.getX(), startingPosition.getY() - ( height / 2 ) );
		Vector2 max = new Vector2 ( startingPosition.getX() + width, startingPosition.getY() + ( height / 2 ) );
		
		Vector2 minRotated = min.rotate(angle);
		Vector2 maxRotated = max.rotate(angle);
		
		// This isn't a great fitting bounding box (Some text will be outside if angle != 0), but it's probably good enough to test for overlaps.
		Box2D bbox = new Box2D(minRotated,maxRotated);
		return bbox;
	}
	
	public void drawTextRadial(PolarVector2 position, String text) {
		
		double height = 10;
		
		PolarVector2 relativePosition = new PolarVector2(0.0, 0.0);
		
		final int margin = 8;
		relativePosition.setRadius(margin);
		
		double angleHeight = 2 * Math.sin(height / (2 * relativePosition.getRadius()));
		relativePosition.setAngle(position.getAngle() + angleHeight / 2);
		
		Vector2 textPosition = position.toCartesian(new Vector2(0.5,0.5));
		double angle = position.getAngle();
		
		Vector2 offset = relativePosition.toCartesian(new Vector2(0.5,0.5));
		
		this.drawText(textPosition, offset, text, angle);
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#drawTriangle(org.iplantc.phyloviewer.client.tree.viewer.math.Vector2, double, double, double)
	 */
	public void drawTriangle(Vector2 v0,double x, double y0, double y1){
		Vector2 v0Prime = matrix.transform(v0);
		Vector2 v1 = new Vector2(x,y0);
		Vector2 v1Prime = matrix.transform(v1);
		Vector2 v2 = new Vector2(x,y1);
		Vector2 v2Prime = matrix.transform(v2);
		
		canvas.beginPath();
		canvas.moveTo(v0Prime.getX(),v0Prime.getY());
		canvas.lineTo(v1Prime.getX(),v1Prime.getY());
		canvas.lineTo(v2Prime.getX(),v2Prime.getY());
		canvas.closePath();
		canvas.fill();
		canvas.stroke();
	}
	
	public void drawWedge(Vector2 peak, PolarVector2 base0, PolarVector2 base1) {
		canvas.save();
		
		Vector2 center = matrix.transform(new Vector2(0.5,0.5));
		peak = matrix.transform(peak).subtract(center);
		base0 = new PolarVector2(matrix.transform(base0.toCartesian(new Vector2(0.5,0.5))).subtract(center));
		base1 = new PolarVector2(matrix.transform(base1.toCartesian(new Vector2(0.5,0.5))).subtract(center));
		double radius = base0.getRadius();
		
		canvas.translate(center.getX(), center.getY());
		canvas.beginPath();
		canvas.moveTo(peak.getX(), peak.getY());
		canvas.lineTo(base0.getX(), base0.getY());
		canvas.arc(0, 0, radius, base0.getAngle(), base1.getAngle(), false);
		canvas.closePath();
		
		canvas.fill();
		canvas.stroke();
		
		canvas.restore();
	}

	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#setViewMatrix(org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33)
	 */
	public void setViewMatrix(Matrix33 matrix) {
		this.matrix = matrix;
		
		Matrix33 IM = matrix.inverse();
		screenBounds.setMin(IM.transform(new Vector2(0,0)));
		screenBounds.setMax(IM.transform(new Vector2(this.canvas.getWidth(),this.canvas.getHeight())));
	}
	
	public Matrix33 getViewMatrix() {
		return this.matrix;
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#isCulled(org.iplantc.phyloviewer.client.tree.viewer.math.Box2D)
	 */
	public Boolean isCulled(Box2D bbox) {
		if ( !bbox.valid() )
			return false;

		return !screenBounds.intersects(bbox);
	}

	@Override
	public void drawArc(Vector2 center, double radius, double startAngle, double endAngle) {
		//note:  I don't think Canvas can draw elliptical arcs, so xzoom and yzoom are assumed to be the same.  Alternatively, the canvas transform could be manipulated here instead of the arc parameters, or the arcs can be approximated with bezier curves.
		center = matrix.transform(center);
		radius = radius * matrix.getScaleY();

		canvas.beginPath();
		canvas.arc(center.getX(), center.getY(), radius, startAngle, endAngle, false);
		canvas.stroke();
	}

	@Override
	public void setStyle(IElementStyle style) {
		canvas.setFillStyle(style.getFillColor());
		canvas.setStrokeStyle(style.getStrokeColor());
		canvas.setLineWidth(style.getLineWidth());
	}
	
	public Box2D getDisplayedBox(Box2D box) {
		return this.matrix.transform(box);
	}
}
