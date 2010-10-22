package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

public class CameraCircular extends Camera {
	static final double labelMargin = 150;

	public Camera create() {
		return new CameraCircular();
	}
	
	public Matrix33 getMatrix() {
		double scale = Math.min(getCanvasWidth(),getCanvasHeight()) - labelMargin;
		double tx = (getCanvasWidth() - getCanvasHeight()) / 2.0;
		tx = Math.max(tx, 0);
		tx += labelMargin / 2.0;
		double ty = (getCanvasHeight() - getCanvasWidth()) / 2.0;
		ty = Math.max(ty, 0);
		ty += labelMargin / 2.0;
		
		Matrix33 s = Matrix33.makeScale(scale, scale);
		Matrix33 t = Matrix33.makeTranslate(tx, ty);
		return t.multiply(s).multiply(this.getViewMatrix());
	}

	public void zoomToFitSubtree(final INode node, final ILayout layout) {
		if (layout instanceof RemoteLayout && !layout.containsNode(node)) {
			
			RemoteLayout rLayout = (RemoteLayout) layout;
			rLayout.getLayoutAsync(node, rLayout.new GotLayout() {
				@Override
				protected void gotLayout(LayoutResponse responses) {
					zoomToFitSubtree(node, layout);
				}
			});
			
		} else {
			
			Box2D bounds = layout.getBoundingBox(node);
			Vector2 position = bounds.getMin();
			
			double xFactor = 1.0 / bounds.getWidth();
			double yFactor = 1.0 / bounds.getHeight();
			double factor = Math.min(xFactor, yFactor);
			
			Matrix33 S = Matrix33.makeScale(factor, factor);
			Matrix33 T1 = Matrix33.makeTranslate(-position.getX(), -position.getY());
			this.setViewMatrix(S.multiply(T1), true);
			
		}
	}
}
