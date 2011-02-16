package org.iplantc.phyloviewer.server.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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

public class Java2DGraphics extends Graphics
{
	private Graphics2D g2d;
	private AffineTransform transform; // this is used to transform paths and shapes for drawing

	/**
	 * Create a new Java2DGraphics that draws on the given Graphics2D
	 * 
	 * @param graphics the drawing target. Note that the Graphics2D user clip <strong>must</strong> be
	 *            set for isCulled() to work.
	 */
	public Java2DGraphics(Graphics2D graphics)
	{
		this.g2d = graphics;
		this.transform = new AffineTransform();

		g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		
		this.setSize(1, 1);
	}

	protected Java2DGraphics()
	{
	}
	
	@Override
	protected void updateMatrix()
	{
		super.updateMatrix();
		this.transform = affineTransformFrom(objectToScreenMatrix);
	}

	protected void setGraphics2D(Graphics2D graphics)
	{
		this.g2d = graphics;
	}

	@Override
	public void clear()
	{
		g2d.setColor(g2d.getBackground());
	}

	@Override
	public void drawArc(Vector2 center, double radius, double startAngle, double endAngle)
	{
		// TODO implement circular rendering methods in Java2DGraphics
		throw new RuntimeException("drawArc not yet implemented");
	}

	@Override
	public void drawPoint(Vector2 position)
	{
		Point2D point = point2DFrom(position);
		transform.transform(point, point);
		double radius = Defaults.POINT_SIZE / 2.0;
		double x = point.getX() - radius;
		double y = point.getY() - radius;
		double width = Defaults.POINT_SIZE;
		Ellipse2D.Double circle = new Ellipse2D.Double(x, y, width, width);
		g2d.fill(circle);
	}

	@Override
	public void drawText(Vector2 position, Vector2 offset, String text, double angle)
	{
		if(text != null)
		{
			Point2D point = point2DFrom(position);
			transform.transform(point, point);
			float x = (float)(point.getX() + offset.getX());
			float y = (float)(point.getY() + offset.getY());
			g2d.drawString(text, x, y);
			// TODO keep track of text bounds and don't write over previously drawn text
		}
	}

	@Override
	public void drawWedge(Vector2 center, Vector2 peak, double radius, double startAngle, double endAngle)
	{
		// TODO implement circular rendering methods in Java2DGraphics
		throw new RuntimeException("drawWedge not yet implemented");
	}

	@Override
	public void setViewMatrix(Matrix33 matrix)
	{
		super.setViewMatrix(matrix);
		this.transform = affineTransformFrom(objectToScreenMatrix);
	}

	public static AffineTransform affineTransformFrom(Matrix33 m)
	{
		return new AffineTransform(m.getScaleX(), m.getShearY(), m.getShearX(), m.getScaleY(),
				m.getTranslationX(), m.getTranslationY());
	}

	public static Matrix33 matrix33From(AffineTransform t)
	{
		return new Matrix33(t.getScaleX(), t.getShearX(), t.getTranslateX(), t.getShearY(),
				t.getScaleY(), t.getTranslateY(), 0, 0, 1);
	}

	public static Point2D point2DFrom(Vector2 v)
	{
		return new Point2D.Double(v.getX(), v.getY());
	}

	public static Rectangle2D rectangle2DFrom(Box2D box)
	{
		return new Rectangle2D.Double(box.getMin().getX(), box.getMin().getY(), box.getWidth(),
				box.getHeight());
	}

	@Override
	public void setStyle(IBranchStyle style)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setStyle(IGlyphStyle style)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setStyle(ILabelStyle style)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setStyle(INodeStyle style)
	{
		// TODO Auto-generated method stub

	}

	private static Path2D createPath(Vector2[] vertices)
	{
		Path2D path = new Path2D.Double();

		if(vertices.length > 2)
		{
			path.moveTo(vertices[0].getX(), vertices[0].getY());
			for(int i = 1;i < vertices.length;++i)
			{
				path.lineTo(vertices[i].getX(), vertices[i].getY());
			}
		}
		return path;
	}

	@Override
	public void drawLineStrip(Vector2[] vertices)
	{
		Path2D path = createPath(vertices);
		g2d.draw(transform.createTransformedShape(path));
	}

	@Override
	public void drawPolygon(Vector2 vertices[])
	{
		Path2D path = createPath(vertices);
		path.closePath();

		Shape transformedPath = transform.createTransformedShape(path);
		g2d.fill(transformedPath);
		g2d.draw(transformedPath);
	}

	@Override
	public Box2D calculateBoundingBox(Text text)
	{
		return text.getBoundingBox();
	}
}
