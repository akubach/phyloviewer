package org.iplantc.phyloviewer.server.render;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.render.Defaults;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.IElementStyle;

public class Java2DGraphics implements IGraphics {
	private final Graphics2D g2d;
	private AffineTransform transform; //this is used to transform paths and shapes for drawing
	private Matrix33 matrix; //this is kept in order to quickly return getViewMatrix() and getDisplayedBox()
	
	public Java2DGraphics(Graphics2D graphics) {
		this.g2d = graphics;
		this.transform = new AffineTransform();
	}
	
	@Override
	public void clear() {
		g2d.setColor(g2d.getBackground());
	}

	@Override
	public void drawArc(Vector2 center, double radius, double startAngle,
			double endAngle) {
		//TODO
		throw new RuntimeException("drawArc not yet implemented");
	}

	@Override
	public void drawLine(Vector2 start, Vector2 end) {
		Line2D line = new Line2D.Double(point2DFrom(start), point2DFrom(end));
		g2d.draw(transform.createTransformedShape(line));
	}

	@Override
	public void drawPoint(Vector2 position) {
		double x = position.getX() - Defaults.POINT_RADIUS;
		double y = position.getY() - Defaults.POINT_RADIUS;
		double width = 2 * Defaults.POINT_RADIUS;
		Ellipse2D.Double circle = new Ellipse2D.Double(x, y, width, width);
		g2d.draw(transform.createTransformedShape(circle));
	}

	@Override
	public void drawRightAngle(Vector2 start, Vector2 end) {
		Path2D path = new Path2D.Double();
		path.moveTo(start.getX(), start.getY());
		path.lineTo(start.getX(), end.getY());
		path.lineTo(end.getX(), end.getY());
		
		g2d.draw(transform.createTransformedShape(path));
	}

	@Override
	public void drawText(Vector2 position, String text) {
		Point2D point = point2DFrom(position);
		transform.transform(point, point);
		g2d.drawString(text, (float)point.getX(), (float)point.getY());
		//TODO keep track of text bounds and don't write over previously drawn text
	}

	@Override
	public void drawTextRadial(PolarVector2 position, String text) {
		// TODO
		throw new RuntimeException("drawTextRadial not yet implemented");
	}

	@Override
	public void drawTriangle(Vector2 v0, double x, double y0, double y1) {
		Vector2 v1 = new Vector2(x,y0);
		Vector2 v2 = new Vector2(x,y1);
		
		Path2D path = new Path2D.Double();
		path.moveTo(v0.getX(),v0.getY());
		path.lineTo(v1.getX(),v1.getY());
		path.lineTo(v2.getX(),v2.getY());
		path.closePath();
		
		Shape transformedPath = transform.createTransformedShape(path);
		g2d.fill(transformedPath);
		g2d.draw(transformedPath);
	}

	@Override
	public void drawWedge(Vector2 peak, PolarVector2 base0, PolarVector2 base1) {
		// TODO
		throw new RuntimeException("drawWedge not yet implemented");
	}

	@Override
	public Boolean isCulled(Box2D iBox2D) {
		Rectangle deviceBox = transform.createTransformedShape(rectangle2DFrom(iBox2D)).getBounds();
		Rectangle deviceBounds = this.g2d.getDeviceConfiguration().getBounds();
		return !deviceBox.intersects(deviceBounds);
	}

	@Override
	public void setStyle(IElementStyle style) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setViewMatrix(Matrix33 matrix) {
		this.matrix = matrix;
		this.transform = affineTransformFrom(matrix);
	}

	@Override
	public Matrix33 getViewMatrix() {
		return matrix; 
	}
	
	@Override
	public Box2D getDisplayedBox(Box2D box) {
		return matrix.transform(box);
	}
	
	public void setAffineTransform(AffineTransform transform) {
		this.transform = transform;
		this.matrix = matrix33From(transform);
	}
	
	public static AffineTransform affineTransformFrom(Matrix33 m) {
		return new AffineTransform(m.getScaleX(), m.getShearY(), m.getShearX(), m.getScaleY() , m.getTranslationX(), m.getTranslationY());
	}
	
	public static Matrix33 matrix33From(AffineTransform t) {
		return new Matrix33(t.getScaleX(), t.getShearX(), t.getTranslateX(), t.getShearY(), t.getScaleY(), t.getTranslateY(), 0, 0, 1);
	}
	
	public static Point2D point2DFrom(Vector2 v) {
		return new Point2D.Double(v.getX(), v.getY());
	}
	
	public static Rectangle2D rectangle2DFrom(Box2D box) {
		return new Rectangle2D.Double(box.getMin().getX(), box.getMin().getY(), box.getWidth(), box.getHeight());
	}
}
