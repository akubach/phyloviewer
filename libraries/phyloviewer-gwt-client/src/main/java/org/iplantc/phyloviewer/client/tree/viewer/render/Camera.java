/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.INode;

public abstract class Camera {

	private Matrix33 _matrix = new Matrix33();

	public Camera() {
	}
	
	public abstract Camera create();
	
	public abstract void zoomToFitSubtree(INode node, ILayout layout);

	public Matrix33 getMatrix(int width, int height) {
		return Matrix33.makeScale(width,height).multiply(_matrix);
	}
	
	public Matrix33 getViewMatrix() {
		return _matrix;
	}
	
	public void setViewMatrix(Matrix33 matrix) {
		_matrix = matrix;
	}
	
	public void zoom(double xCenter, double yCenter, double xZoom, double yZoom) {
		Matrix33 T0 = Matrix33.makeTranslate(xCenter, yCenter);
		Matrix33 S = Matrix33.makeScale(xZoom, yZoom);
		Matrix33 T1 = Matrix33.makeTranslate(-xCenter, -yCenter);
		
		Matrix33 delta = T0.multiply(S.multiply(T1));
		Matrix33 matrix = delta.multiply(_matrix);
		this.setViewMatrix(matrix);
	}
	
	public void zoom(double factor) {
		zoom(0.5, 0.5, factor, factor);
	}
	
	public void pan(double x, double y) {
		Matrix33 matrix = _matrix.multiply(Matrix33.makeTranslate(x,y));
		this.setViewMatrix(matrix);
	}
	
	public void reset() {
		this.setViewMatrix(new Matrix33());
	}
}
