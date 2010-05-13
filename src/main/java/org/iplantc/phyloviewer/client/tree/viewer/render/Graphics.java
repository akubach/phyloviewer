package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;


public class Graphics {

	private Canvas canvas = null;
	private Matrix33 matrix = new Matrix33();
	private Box2D screenBounds = new Box2D();
	
	public Graphics(Canvas canvas) {
		this.canvas = canvas;
		screenBounds.setMin(new Vector2(0,0));
		screenBounds.setMax(new Vector2(canvas.getWidth(),canvas.getHeight()));
	}
	
	public void clear() {
		canvas.clear();
	}
	
	public void drawPoint(Vector2 position) {
		Vector2 p = matrix.transform(position);
		
		canvas.setFillStyle(Defaults.POINT_COLOR);
		canvas.beginPath();
		canvas.arc(p.getX(), p.getY(), Defaults.POINT_RADIUS, 0, Math.PI*2, true); 
		canvas.closePath();
		canvas.fill();
	}
	
	public void drawLine(Vector2 start, Vector2 end) {
		Vector2 p0 = matrix.transform(start);
		Vector2 p1 = matrix.transform(end);
		
		canvas.setFillStyle(Defaults.LINE_COLOR);
		canvas.beginPath();
		canvas.moveTo(p0.getX(),p0.getY());
		canvas.lineTo(p0.getX(),p1.getY());
		canvas.lineTo(p1.getX(),p1.getY());
		canvas.stroke();
	}
	
	public void drawText(Vector2 position, String text) {
		Vector2 p = matrix.transform(position);
		
		canvas.setStrokeStyle(Defaults.TEXT_COLOR);
		canvas.setFillStyle(Defaults.TEXT_COLOR);
		canvas.fillText(text, p.getX() + 7, p.getY() + 2);
	}
	
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

	public void setViewMatrix(Matrix33 matrix) {
		this.matrix = matrix;
		
		Matrix33 IM = matrix.inverse();
		screenBounds.setMin(IM.transform(new Vector2(0,0)));
		screenBounds.setMax(IM.transform(new Vector2(this.canvas.getWidth(),this.canvas.getHeight())));
	}
	
	public Boolean isCulled(Box2D bbox) {
		if ( !bbox.valid() )
			return false;

		return !screenBounds.intersects(bbox);
	}
}
