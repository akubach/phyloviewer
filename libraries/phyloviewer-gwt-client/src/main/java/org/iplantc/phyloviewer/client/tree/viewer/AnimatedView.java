package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.EventFactory;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Camera;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;

public abstract class AnimatedView extends View {

	public AnimatedView(EventBus eventBus)
	{
		super(eventBus);
	}
	
	private AnimateCamera animator;
	private Timer renderTimer = new Timer() {
		public void run() {
			if (AnimatedView.this.isReady()) {
				renderFrame();
			} else {
				this.schedule(33);
			}
		}
	};

	private void renderFrame() {
		if (animator != null) {
			getCamera().setViewMatrix(animator.getNextMatrix());

			if (animator.isDone()) {
				animator = null;

				renderTimer.cancel();
			}
		}
		
		// Dispatch a render event.  This will make sure all views are updated with this camera.
		this.dispatch(EventFactory.createRenderEvent());
	}

	protected void startAnimation(Camera finalCamera) {
		animator = new AnimateCamera(getCamera().getViewMatrix(), finalCamera.getViewMatrix(), 25);
		renderTimer.scheduleRepeating(30);
	}

	public void animateZoomToNode(INode node) {
		Camera finalCamera = getCamera().create();
		finalCamera.zoomToFitSubtree(node, getLayout());

		startAnimation(finalCamera);
	}
	
	public void zoomToBoundingBox(Vector2 position,Box2D boundingBox) 
	{
		Camera finalCamera = getCamera().create();
		finalCamera.zoomToBoundingBox(position, boundingBox);

		startAnimation(finalCamera);
	}
}
