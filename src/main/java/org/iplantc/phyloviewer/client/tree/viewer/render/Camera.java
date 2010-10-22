/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.INode;

public abstract class Camera {

	private Matrix33 _matrix = new Matrix33();
	private int canvasWidth=1;
	private int canvasHeight=1;
	private List<CameraChangedHandler> listeners = new ArrayList<CameraChangedHandler>();
	
	public Camera() {
	}
	
	public abstract Camera create();
	
	public abstract void zoomToFitSubtree(INode node, ILayout layout);
	
	public void resize(int width, int height) {
		setCanvasWidth(width);
		setCanvasHeight(height);
	}
	
	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public int getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasHeight(int canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	public int getCanvasHeight() {
		return canvasHeight;
	}

	public Matrix33 getMatrix() {
		return Matrix33.makeScale(getCanvasWidth(),getCanvasHeight()).multiply(_matrix);
	}
	
	public Matrix33 getViewMatrix() {
		return _matrix;
	}
	
	public void setViewMatrix(Matrix33 matrix, boolean notify) {
		_matrix = matrix;
		
		if (notify) {
			for ( CameraChangedHandler handler : listeners ) {
				handler.onCameraChanged();
			}
		}
	}
	
	public void zoom(double xCenter, double yCenter, double xZoom, double yZoom) {
		Matrix33 T0 = Matrix33.makeTranslate(xCenter, yCenter);
		Matrix33 S = Matrix33.makeScale(xZoom, yZoom);
		Matrix33 T1 = Matrix33.makeTranslate(-xCenter, -yCenter);
		
		Matrix33 delta = T0.multiply(S.multiply(T1));
		Matrix33 matrix = delta.multiply(_matrix);
		this.setViewMatrix(matrix, true);
	}
	
	public void zoom(double factor) {
		zoom(0.5, 0.5, factor, factor);
	}
	
	public void pan(double x, double y) {
		Matrix33 matrix = _matrix.multiply(Matrix33.makeTranslate(x,y));
		this.setViewMatrix(matrix, true);
	}
	
	public void addCameraChangedHandler(CameraChangedHandler handler) {
		if ( handler != null ) {
			listeners.add(handler);
		}
	}
	
	public void reset() {
		this.setViewMatrix(new Matrix33(), true);
	}
}
