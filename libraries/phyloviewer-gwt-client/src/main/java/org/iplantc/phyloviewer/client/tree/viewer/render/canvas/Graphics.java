/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render.canvas;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;

import com.google.gwt.user.client.ui.Widget;


public class Graphics implements IGraphics {

	private static final double DEGREES_270 = 3 * Math.PI / 2;
	private static final double DEGREES_90 = Math.PI / 2;
	private Canvas canvas = null;
	private Matrix33 matrix = new Matrix33();
	private Box2D screenBounds = new Box2D();
	private List<Box2D> drawnTextExtents = new ArrayList<Box2D>();
	private double pointRadius = Defaults.POINT_RADIUS;
	int width;
	int height;
	
	public Graphics(int width, int height) {
		this.canvas = new Canvas(width,height);
		this.width=width;
		this.height=height;
		
		screenBounds.setMin(new Vector2(0,0));
		screenBounds.setMax(new Vector2(width,height));
	}
	
	public Widget getWidget() {
		return canvas;
	}
	
	// TODO: need to change the api so this method can be removed.
	public Canvas getCanvas() {
		return canvas;
	}
	
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		
		canvas.setWidth(width);
		canvas.setHeight(height);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
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
		canvas.arc(p.getX(), p.getY(), pointRadius, 0, Math.PI*2, true); 
		canvas.closePath();
		canvas.fill();
	}
	
	@Override
	public void drawLineStrip(Vector2[] vertices) {
		if (vertices.length < 2 ) {
			return;
		}
		
		canvas.beginPath();
		
		Vector2 vector = matrix.transform(vertices[0]);
		canvas.moveTo(vector.getX(),vector.getY());
		
		for(int i = 1; i < vertices.length; ++i ) {
			vector = matrix.transform(vertices[i]);
			canvas.lineTo(vector.getX(),vector.getY());
		}

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
		
		// This bounding box does a much better job of enclosing text, but it culls too much in circular layout.
		/*Vector2 min = new Vector2 ( startingPosition.getX(), startingPosition.getY() );
		Vector2 max = new Vector2 ( startingPosition.getX() + width, startingPosition.getY() );
	
		Vector2 rotatePoint = startingPosition;
		Vector2 minRotated = ((min.subtract(rotatePoint)).rotate(angle)).add(rotatePoint);
		Vector2 maxRotated = ((max.subtract(rotatePoint)).rotate(angle)).add(rotatePoint);
		
		Box2D bbox = new Box2D();
		bbox.expandBy(minRotated);
		bbox.expandBy(maxRotated);
		bbox.expandBy(height);
		return bbox;*/
	
		Vector2 min = new Vector2 ( startingPosition.getX(), startingPosition.getY() - ( height / 2 ) );
		Vector2 max = new Vector2 ( startingPosition.getX() + width, startingPosition.getY() + ( height / 2 ) );
		return new Box2D(min,max);
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
	
	@Override
	public void drawPolygon(Vector2 vertices[]) {
		
		if (vertices.length < 3 ) {
			return;
		}
		
		canvas.beginPath();
		
		Vector2 vector = matrix.transform(vertices[0]);
		canvas.moveTo(vector.getX(),vector.getY());
		
		for(int i = 1; i < vertices.length; ++i ) {
			vector = matrix.transform(vertices[i]);
			canvas.lineTo(vector.getX(),vector.getY());
		}

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
		screenBounds.setMax(IM.transform(new Vector2(width,height)));
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

	
	public Box2D getDisplayedBox(Box2D box) {
		return this.matrix.transform(box);
	}

	@Override
	public void setStyle(IBranchStyle style) {
		try
		{
			if (style != null)
			{
				if (style.getStrokeColor() != null)
				{
					canvas.setStrokeStyle(style.getStrokeColor());
				}
				
				if (!Double.isNaN(style.getLineWidth()))
				{
					canvas.setLineWidth(style.getLineWidth()); 
				}
			}
		}
		catch ( Exception e ) {
			canvas.setStrokeStyle(Defaults.LINE_COLOR);
			canvas.setLineWidth(1.0);
		}
	}

	@Override
	public void setStyle(IGlyphStyle style) {
		try
		{
			if (style != null)
			{
				if (style.getFillColor() != null)
				{
					canvas.setFillStyle(style.getFillColor());
				}
				
				if (style.getStrokeColor() != null)
				{
					canvas.setStrokeStyle(style.getStrokeColor());
				}
				
				if (!Double.isNaN(style.getLineWidth()))
				{
					canvas.setLineWidth(style.getLineWidth()); 
				}
			}
		}
		catch ( Exception e ) {
			canvas.setFillStyle(Defaults.TRIANGLE_FILL_COLOR);
			canvas.setStrokeStyle(Defaults.TRIANGLE_OUTLINE_COLOR);
			canvas.setLineWidth(1.0);
		}
	}

	@Override
	public void setStyle(ILabelStyle style) {
		try
		{
			if (style != null)
			{
				if (style.getColor() != null)
				{
					canvas.setFillStyle(style.getColor());
				}
			}
		}
		catch ( Exception e ) {
			canvas.setFillStyle(Defaults.TEXT_COLOR);
		}
	}

	@Override
	public void setStyle(INodeStyle style) {
		try
		{
			if (style != null)
			{
				if (style.getColor() != null)
				{
					canvas.setFillStyle(style.getColor());
					canvas.setStrokeStyle(style.getColor());
				}
				
				if (!Double.isNaN(style.getPointSize()))
				{
					this.pointRadius = style.getPointSize(); 
				}
			}
		}
		catch ( Exception e ) {
			canvas.setFillStyle(Defaults.LINE_COLOR);
			canvas.setStrokeStyle(Defaults.LINE_COLOR);
			canvas.setLineWidth(1.0);
		}
	}
}
