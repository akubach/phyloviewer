package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;

public class Camera {

	private Matrix33 _matrix = new Matrix33();
	private int _canvasWidth=1;
	private int _canvasHeight=1;
	
	public Camera() {
	}
	
	public void resize(int width, int height) {
		_canvasWidth=width;
		_canvasHeight=height;
	}
	
	public Matrix33 getMatrix() {
		return Matrix33.makeScale(_canvasWidth,_canvasHeight).multiply(_matrix);
	}
	
	public Matrix33 getViewMatrix() {
		return _matrix;
	}
	
	public void zoomInYDirection(double amount) {
		Matrix33 T0 = Matrix33.makeTranslate(0.0, 0.5);
		Matrix33 S = Matrix33.makeScale(1, Math.pow(2, amount));
		Matrix33 T1 = Matrix33.makeTranslate(0.0, -0.5);
		
		Matrix33 delta = T0.multiply(S.multiply(T1));
		_matrix = delta.multiply(_matrix);
	}
	
	public void panY ( double amount) {
		_matrix = _matrix.multiply(Matrix33.makeTranslate(0.0,amount));
	}
}
