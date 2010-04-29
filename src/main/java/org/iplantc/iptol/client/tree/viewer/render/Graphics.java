package org.iplantc.iptol.client.tree.viewer.render;

import org.iplantc.iptol.client.tree.viewer.canvas.Canvas;
import org.iplantc.iptol.client.tree.viewer.math.Box2D;
import org.iplantc.iptol.client.tree.viewer.math.Matrix33;
import org.iplantc.iptol.client.tree.viewer.math.Vector2;


public class Graphics {

	private Canvas _canvas = null;
	private Matrix33 _matrix = new Matrix33();
	private Box2D _screenBounds = new Box2D();
	
	public Graphics(Canvas canvas) {
		_canvas = canvas;
		_screenBounds.setMin(new Vector2(0,0));
		_screenBounds.setMax(new Vector2(canvas.getWidth(),canvas.getHeight()));
	}
	
	public void clear() {
		_canvas.clear();
	}
	
	public void drawPoint(Vector2 position) {
		Vector2 p = _matrix.transform(position);
		
		_canvas.setFillStyle("black");
		_canvas.beginPath();
		_canvas.arc(p.getX(), p.getY(), 3, 0, Math.PI*2, true); 
		_canvas.closePath();
		_canvas.fill();
	}
	
	public void drawLine(Vector2 start, Vector2 end) {
		Vector2 p0 = _matrix.transform(start);
		Vector2 p1 = _matrix.transform(end);
		
		_canvas.beginPath();
		_canvas.moveTo(p0.getX(),p0.getY());
		_canvas.lineTo(p0.getX(),p1.getY());
		_canvas.lineTo(p1.getX(),p1.getY());
		_canvas.stroke();
	}
	
	public void drawText(Vector2 position, String text) {
		Vector2 p = _matrix.transform(position);
		
		_canvas.setFillStyle("black");
		_canvas.fillText(text, p.getX() + 7, p.getY() + 2);
	}
	
	public void drawTriangle(Vector2 v0,double x, double y0, double y1){
		Vector2 v0Prime = _matrix.transform(v0);
		Vector2 v1 = new Vector2(x,y0);
		Vector2 v1Prime = _matrix.transform(v1);
		Vector2 v2 = new Vector2(x,y1);
		Vector2 v2Prime = _matrix.transform(v2);
		
		_canvas.setFillStyle("green");
		_canvas.beginPath();
		_canvas.moveTo(v0Prime.getX(),v0Prime.getY());
		_canvas.lineTo(v1Prime.getX(),v1Prime.getY());
		_canvas.lineTo(v2Prime.getX(),v2Prime.getY());
		_canvas.closePath();
		_canvas.fill();
		_canvas.stroke();
	}

	public void setViewMatrix(Matrix33 matrix) {
		_matrix = matrix;
		
		Matrix33 IM = matrix.inverse();
		_screenBounds.setMin(IM.transform(new Vector2(0,0)));
		_screenBounds.setMax(IM.transform(new Vector2(this._canvas.getWidth(),this._canvas.getHeight())));
	}
	
	public Boolean isCulled(Box2D bbox) {
		if ( !bbox.valid() )
			return false;
		//return false;
		return !_screenBounds.intersects(bbox);
	}
}
