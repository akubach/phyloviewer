package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public class CameraCircular extends Camera {

	public Camera create() {
		return new CameraCircular();
	}
	
	public Matrix33 getMatrix() {
		double canvasScale = Math.min(getCanvasWidth(),getCanvasHeight());
		return Matrix33.makeScale(canvasScale, canvasScale).multiply(this.getViewMatrix());
	}

	@Override
	public void zoomToFitSubtree(INode node, ILayout layout) {
		// FIXME
		Vector2 position = layout.getPosition(node);
		Vector2 center = new Vector2 ( 0.5, 0.5 );
		Vector2 delta = new Vector2 ( position.getX() - center.getX(), position.getY() - center.getY() );

		// Translate node to center.
		Matrix33 matrix = this.getViewMatrix();
		matrix = Matrix33.makeTranslate(-delta.getX(), -delta.getY()).multiply(matrix);
		this.setViewMatrix(matrix, true);
		
	}
}
