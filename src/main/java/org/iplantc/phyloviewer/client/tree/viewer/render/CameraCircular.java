package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public class CameraCircular extends Camera {

	public Camera create() {
		return new CameraCircular();
	}
	
	public Matrix33 getMatrix() {
		return Matrix33.makeScale(getCanvasHeight(),getCanvasHeight()).multiply(this.getViewMatrix());
	}
	
	public void zoomInYDirection(double amount) {
		this.zoom(0.5, 0.5, Math.pow(2, amount), Math.pow(2, amount));
	}
	
	public void panX ( double amount ) {
		Matrix33 matrix = this.getViewMatrix().multiply(Matrix33.makeTranslate(amount,0));
		this.setViewMatrix(matrix);
	}
	
	public void zoomToNode(INode node, ILayout layout) {
		Vector2 postion = layout.getPosition(node);

		Matrix33 matrix = this.getViewMatrix();
		matrix.setTranslationX(postion.getX()-0.5);
		matrix.setTranslationY(postion.getY()-0.5);
		this.setViewMatrix(matrix);
	}
}
