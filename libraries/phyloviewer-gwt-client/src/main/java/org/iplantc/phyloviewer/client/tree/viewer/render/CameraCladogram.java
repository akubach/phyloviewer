package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.INode;

public class CameraCladogram extends Camera {
	static final double zoomAboutX = 0.8;

	public Camera create() {
		return new CameraCladogram();
	}

	public void panX(double dx) {
		Matrix33 T0 = Matrix33.makeTranslate(zoomAboutX, 0.0);
		Matrix33 S = Matrix33.makeScale(Math.max(1 + dx, 0.01),1.0);
		Matrix33 T1 = Matrix33.makeTranslate(-zoomAboutX, 0.0);
		
		Matrix33 delta = T0.multiply(S.multiply(T1));
		Matrix33 matrix = delta.multiply(this.getViewMatrix());
		this.setViewMatrix(matrix);
	}
	
	@Override
	public void zoom(double factor) {
		zoom(0.0, 0.5, 1.0, factor);
	}
	
	@Override
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
	}
}
