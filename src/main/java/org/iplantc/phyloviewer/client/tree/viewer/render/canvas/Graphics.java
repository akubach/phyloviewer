package org.iplantc.phyloviewer.client.tree.viewer.render.canvas;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.render.Defaults;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;


public class Graphics implements IGraphics {

	private Canvas canvas = null;
	private Matrix33 matrix = new Matrix33();
	private Box2D screenBounds = new Box2D();
	private List<Box2D> drawnTextExtents = new ArrayList<Box2D>();
	private List<AnnularSector> radialTextExtents = new ArrayList<AnnularSector>();
	
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
		radialTextExtents.clear();
		canvas.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#drawPoint(org.iplantc.phyloviewer.client.tree.viewer.math.Vector2)
	 */
	public void drawPoint(Vector2 position) {
		Vector2 p = matrix.transform(position);
		
		canvas.setFillStyle(Defaults.POINT_COLOR);
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
		
		canvas.setFillStyle(Defaults.LINE_COLOR);
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
		
		canvas.setFillStyle(Defaults.LINE_COLOR);
		canvas.beginPath();
		canvas.moveTo(p0.getX(),p0.getY());
		canvas.lineTo(p1.getX(),p1.getY());
		canvas.stroke();
	}
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics#drawText(org.iplantc.phyloviewer.client.tree.viewer.math.Vector2, java.lang.String)
	 */
	public void drawText(Vector2 position, String text) {
		Vector2 p = matrix.transform(position);
		
		canvas.setStrokeStyle(Defaults.TEXT_COLOR);
		canvas.setFillStyle(Defaults.TEXT_COLOR);
		
		Vector2 startingPosition = new Vector2 ( p.getX() + 7, p.getY() + 2 );
		
		// TODO: Get the text height from the canvas.
		float height = 10;
		
		// Make a bounding box of the text.  For now the width doesn't matter.
		Vector2 min = new Vector2 ( startingPosition.getX(), startingPosition.getY() - ( height / 2 ) );
		Vector2 max = new Vector2 ( startingPosition.getX() + 100, startingPosition.getY() + ( height / 2 ) );
		Box2D bbox = new Box2D(min,max);
		
		// If this bounding box will intersect any text that we have already drawn, don't draw.
		// If this brute force search proves to be too slow, perhaps a quad tree search would be better.
		for ( Box2D box : drawnTextExtents ) {
			if ( box.intersects(bbox)) {
				return;
			}
		}
		
		canvas.fillText(text, startingPosition.getX(), startingPosition.getY());
		drawnTextExtents.add(bbox);
	}
	
	public void drawTextRadial(PolarVector2 position, String text) {
		canvas.save();
		
		// TODO: Get the text height from the canvas.
		double height = 10;
		int margin = 7;
		
		Vector2 center = matrix.transform(new Vector2(0,0));
		PolarVector2 relativePosition = new PolarVector2(matrix.transform(position).substract(center));
		relativePosition.setRadius(relativePosition.getRadius() + margin);
		double angleHeight = 2 * Math.sin(height / (2 * relativePosition.getRadius()));

		// Make a bounding box of the text.  For now the width doesn't matter.
		PolarVector2 min = new PolarVector2 ( relativePosition.getRadius(), relativePosition.getAngle() - ( angleHeight / 2 ) );
		PolarVector2 max = new PolarVector2 ( relativePosition.getRadius() + 100, relativePosition.getAngle() + ( angleHeight / 2 ) );
		AnnularSector polarBounds = new AnnularSector(min,max);

		for ( AnnularSector box : radialTextExtents ) {
			if ( polarBounds.intersects(box)) {
				return;
			}
		}
		radialTextExtents.add(polarBounds);
		
		canvas.translate(center.getX(), center.getY());
		canvas.rotate(relativePosition.getAngle());
		
		canvas.setStrokeStyle(Defaults.TEXT_COLOR);
		canvas.setFillStyle(Defaults.TEXT_COLOR);
		canvas.fillText(text, relativePosition.getRadius(), 0.0);
		
		canvas.restore();
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
		
		canvas.setStrokeStyle(Defaults.TRIANGLE_OUTLINE_COLOR);
		canvas.setFillStyle(Defaults.TRIANGLE_FILL_COLOR);
		canvas.beginPath();
		canvas.moveTo(v0Prime.getX(),v0Prime.getY());
		canvas.lineTo(v1Prime.getX(),v1Prime.getY());
		canvas.lineTo(v2Prime.getX(),v2Prime.getY());
		canvas.closePath();
		canvas.fill();
		canvas.stroke();
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
		radius = radius * matrix.getScaleX();
		canvas.setFillStyle(Defaults.LINE_COLOR);
		canvas.beginPath();
		canvas.arc(center.getX(), center.getY(), radius, startAngle, endAngle, false);
		canvas.stroke();
	}
}
