/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public abstract class Camera {

	private Matrix33 _matrix = new Matrix33();
	private int canvasWidth=1;
	private int canvasHeight=1;
	private List<CameraChangedHandler> listeners = new ArrayList<CameraChangedHandler>();
	
	public Camera() {
	}
	
	public abstract Camera create();
	
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
	
	// Don't notify, this should probably be a argument.
	public void setMatrix(Matrix33 matrix) {
		this._matrix = matrix;
	}
	
	protected void setViewMatrix(Matrix33 matrix) {
		_matrix = matrix;
		
		for ( CameraChangedHandler handler : listeners ) {
			handler.onCameraChanged();
		}
	}
	
	public void zoomInYDirection(double amount) {
		this.zoom(0.0, 0.5, 1.0, Math.pow(2, amount));
	}
	
	protected void zoom(double xCenter, double yCenter, double xZoom, double yZoom) {
		Matrix33 T0 = Matrix33.makeTranslate(xCenter, yCenter);
		Matrix33 S = Matrix33.makeScale(xZoom, yZoom);
		Matrix33 T1 = Matrix33.makeTranslate(-xCenter, -yCenter);
		
		Matrix33 delta = T0.multiply(S.multiply(T1));
		Matrix33 matrix = delta.multiply(_matrix);
		this.setViewMatrix(matrix);
	}
	
	public void panY ( double amount) {
		Matrix33 matrix = _matrix.multiply(Matrix33.makeTranslate(0.0,amount));
		this.setViewMatrix(matrix);
	}
	
	public void panX ( double amount ) {
		final double zoomAboutX = 0.8;
	  
		Matrix33 T0 = Matrix33.makeTranslate(zoomAboutX, 0.0);
		Matrix33 S = Matrix33.makeScale(Math.max(1 + amount, 0.01),1.0);
		Matrix33 T1 = Matrix33.makeTranslate(-zoomAboutX, 0.0);
		
		Matrix33 delta = T0.multiply(S.multiply(T1));
		Matrix33 matrix = delta.multiply(_matrix);
		this.setViewMatrix(matrix);
	}

	public void pan(double x, double y) {
		Matrix33 matrix = _matrix.multiply(Matrix33.makeTranslate(x,y));
		this.setViewMatrix(matrix);
	}
	
	public void addCameraChangedHandler(CameraChangedHandler handler) {
		if ( handler != null ) {
			listeners.add(handler);
		}
	}

	public void zoomToNode(INode node, ILayout layout) {
		
		Box2D boundingBox = layout.getBoundingBox(node);
		
		if ( boundingBox != null && boundingBox.valid() ) {
			
		    double yPosition = 0.5 - boundingBox.getCenter().getY();

		    double horizontalScale = ( 1 != boundingBox.getMin().getX() ? -0.8 / ( boundingBox.getMin().getX() - 0.8 ) : 0.8 );

		    double boundingBoxHeight = boundingBox.getMax().getY() - boundingBox.getMin().getY();

		    if ( boundingBoxHeight > 0 )
		    {
		    	double verticalScale = 1.0 / boundingBoxHeight;

		    	Matrix33 T0 = Matrix33.makeTranslate(0.8, 0.5);
				Matrix33 SY = Matrix33.makeScale(1, verticalScale);
				Matrix33 TY = Matrix33.makeTranslate(0.0, yPosition);
				Matrix33 SX = Matrix33.makeScale(horizontalScale, 1);
				Matrix33 T1 = Matrix33.makeTranslate(-0.8, -0.5);
				
				Matrix33 matrix = T0.multiply(SY.multiply(TY.multiply(SX.multiply(T1))));
				this.setViewMatrix(matrix);
		    }
		}	
	}
	
	public void reset() {
		this.setViewMatrix(new Matrix33());
	}
}
