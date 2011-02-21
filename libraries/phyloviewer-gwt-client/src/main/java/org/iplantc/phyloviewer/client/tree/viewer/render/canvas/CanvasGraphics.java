/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render.canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.Graphics;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.scene.Text;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.ui.Widget;

public class CanvasGraphics extends Graphics
{
	private static Logger rootLogger = Logger.getLogger("");
	private static final double DEGREES_270 = 3 * Math.PI / 2;
	private static final double DEGREES_90 = Math.PI / 2;
	private Canvas canvas = null;

	private List<Box2D> drawnTextExtents = new ArrayList<Box2D>();
	private double pointSize = Defaults.POINT_SIZE;
	String textColor = "black";

	public CanvasGraphics(Canvas canvas)
	{
		this.canvas = canvas;
	}

	public Widget getWidget()
	{
		return canvas;
	}

	// TODO: need to change the api so this method can be removed.
	public Canvas getCanvas()
	{
		return canvas;
	}

	
	/**
	 * Clear the canvas.
	 */
	@Override
	public void clear()
	{
		drawnTextExtents.clear();
		canvas.clear();
	}
	
	public void clearDrawnTextExtents()
	{
		drawnTextExtents.clear();
	}

	/**
	 * Draw a point at given position.
	 */
	@Override
	public void drawPoint(Vector2 position)
	{
		Vector2 p = objectToScreenMatrix.transform(position);

		canvas.beginPath();
		canvas.arc(p.getX(), p.getY(), pointSize / 2.0, 0, Math.PI * 2, true);
		canvas.closePath();
		canvas.fill();
	}

	@Override
	public void drawLineStrip(Vector2[] vertices)
	{
		if(vertices.length < 2)
		{
			return;
		}

		canvas.beginPath();

		Vector2 vector = objectToScreenMatrix.transform(vertices[0]);
		canvas.moveTo(vector.getX(), vector.getY());

		for(int i = 1;i < vertices.length;++i)
		{
			vector = objectToScreenMatrix.transform(vertices[i]);
			canvas.lineTo(vector.getX(), vector.getY());
		}

		canvas.stroke();
	}

	@Override
	public void drawText(Vector2 position, Vector2 offset, String text, double angle)
	{
		try
		{
			if(text == null || text.equals(""))
			{
				return;
			}

			Vector2 p = objectToScreenMatrix.transform(position);

			Vector2 startingPosition = new Vector2(p.getX() + offset.getX(), p.getY() + offset.getY());

			// TODO: Get the text height from the canvas.
			float height = 10;
			double width = canvas.measureText(text);

			// Make a bounding box of the text.
			Box2D bbox = createBoundingBox(startingPosition, height, width, angle);

			// If this bounding box will intersect any text that we have already drawn, don't draw.
			// If this brute force search proves to be too slow, perhaps a quad tree search would be
			// better.
			for(Box2D box : drawnTextExtents)
			{
				if(box.intersects(bbox))
				{
					return;
				}
			}

			canvas.save();
			canvas.translate(startingPosition.getX(), startingPosition.getY());
			canvas.rotate(angle);

			if(angle > DEGREES_90 && angle < DEGREES_270)
			{
				// flip labels on the left side of the circle so they are right-side-up
				canvas.translate(width, -height);
				canvas.rotate(Math.PI);
			}

			canvas.setFillStyle(textColor);
			canvas.fillText(text, 0.0, 0.0);
			drawnTextExtents.add(bbox);

			canvas.restore();
		}
		catch(JavaScriptException e)
		{
			rootLogger.log(Level.WARNING,
					"An exception was caught in Canvas.drawText: " + e.getMessage());
		}
	}

	@Override
	public Box2D calculateBoundingBox(Text text)
	{
		Vector2 position = text.getPosition();
		Vector2 offset = text.getPixelOffset();
		String textValue = text.getText();
		double angle = text.getAngle();

		Vector2 p = objectToScreenMatrix.transform(position);

		Vector2 startingPosition = new Vector2(p.getX() + offset.getX(), p.getY() + offset.getY());

		// TODO: Get the text height from the canvas.
		float height = 10;
		double width = canvas.measureText(textValue);

		// Make a bounding box of the text.
		Box2D bbox = createBoundingBox(startingPosition, height, width, angle);

		Matrix33 IM = objectToScreenMatrix.inverse();
		bbox = IM.transform(bbox);
		return bbox;
	}

	private Box2D createBoundingBox(Vector2 startingPosition, float height, double width, double angle)
	{

		// This bounding box does a much better job of enclosing text, but it culls too much in circular
		// layout.
		// We probably need an object oriented bounding box instead of a axis aligned bounding box.
		/*
		 * Vector2 min = new Vector2 ( startingPosition.getX(), startingPosition.getY() ); Vector2 max =
		 * new Vector2 ( startingPosition.getX() + width, startingPosition.getY() );
		 * 
		 * Vector2 rotatePoint = startingPosition; Vector2 minRotated =
		 * ((min.subtract(rotatePoint)).rotate(angle)).add(rotatePoint); Vector2 maxRotated =
		 * ((max.subtract(rotatePoint)).rotate(angle)).add(rotatePoint);
		 * 
		 * Box2D bbox = new Box2D(); bbox.expandBy(minRotated); bbox.expandBy(maxRotated);
		 * bbox.expandBy(height); return bbox;
		 */

		Vector2 min = new Vector2(startingPosition.getX(), startingPosition.getY() - (height / 2));
		Vector2 max = new Vector2(startingPosition.getX() + width, startingPosition.getY()
				+ (height / 2));
		return new Box2D(min, max);
	}

	@Override
	public void drawPolygon(Vector2 vertices[])
	{
		if(vertices.length < 3)
		{
			return;
		}

		canvas.beginPath();

		Vector2 vector = objectToScreenMatrix.transform(vertices[0]);
		canvas.moveTo(vector.getX(), vector.getY());

		for(int i = 1;i < vertices.length;++i)
		{
			vector = objectToScreenMatrix.transform(vertices[i]);
			canvas.lineTo(vector.getX(), vector.getY());
		}

		canvas.closePath();
		canvas.fill();
		canvas.stroke();
	}

	@Override
	public void drawWedge(Vector2 center, Vector2 peak, double radius, double startAngle, double endAngle)
	{
		canvas.save();

		center = objectToScreenMatrix.transform(center);
		peak = objectToScreenMatrix.transform(peak);
		radius = radius * objectToScreenMatrix.getScaleY();

		canvas.beginPath();
		canvas.moveTo(peak.getX(), peak.getY());
		canvas.arc(center.getX(), center.getY(), radius, startAngle, endAngle, false);
		canvas.closePath();

		canvas.fill();
		canvas.stroke();

		canvas.restore();
	}

	@Override
	public void drawArc(Vector2 center, double radius, double startAngle, double endAngle)
	{
		// note: I don't think Canvas can draw elliptical arcs, so xzoom and yzoom are assumed to be the
		// same. Alternatively, the canvas transform could be manipulated here instead of the arc
		// parameters, or the arcs can be approximated with bezier curves.
		center = objectToScreenMatrix.transform(center);
		radius = radius * objectToScreenMatrix.getScaleY();

		canvas.beginPath();
		canvas.arc(center.getX(), center.getY(), radius, startAngle, endAngle, false);
		canvas.stroke();
	}

	@Override
	public void setStyle(IBranchStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getStrokeColor() != null)
				{
					canvas.setStrokeStyle(style.getStrokeColor());
				}

				if(!Double.isNaN(style.getLineWidth()))
				{
					canvas.setLineWidth(style.getLineWidth());
				}
			}
		}
		catch(Exception e)
		{
			canvas.setStrokeStyle(Defaults.LINE_COLOR);
			canvas.setLineWidth(1.0);
		}
	}

	@Override
	public void setStyle(IGlyphStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getFillColor() != null)
				{
					canvas.setFillStyle(style.getFillColor());
				}

				if(style.getStrokeColor() != null)
				{
					canvas.setStrokeStyle(style.getStrokeColor());
				}

				if(!Double.isNaN(style.getLineWidth()))
				{
					canvas.setLineWidth(style.getLineWidth());
				}
			}
		}
		catch(Exception e)
		{
			canvas.setFillStyle(Defaults.TRIANGLE_FILL_COLOR);
			canvas.setStrokeStyle(Defaults.TRIANGLE_OUTLINE_COLOR);
			canvas.setLineWidth(1.0);
		}
	}

	@Override
	public void setStyle(ILabelStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getColor() != null)
				{
					textColor = style.getColor();
				}
			}
		}
		catch(Exception e)
		{
			canvas.setFillStyle(Defaults.TEXT_COLOR);
		}
	}

	@Override
	public void setStyle(INodeStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getColor() != null)
				{
					canvas.setFillStyle(style.getColor());
					canvas.setStrokeStyle(style.getColor());
				}

				if(!Double.isNaN(style.getPointSize()))
				{
					this.pointSize = style.getPointSize();
				}
			}
		}
		catch(Exception e)
		{
			canvas.setFillStyle(Defaults.LINE_COLOR);
			canvas.setStrokeStyle(Defaults.LINE_COLOR);
			canvas.setLineWidth(1.0);
		}
	}
}
