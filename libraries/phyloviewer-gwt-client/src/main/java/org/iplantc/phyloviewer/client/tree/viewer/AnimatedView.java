package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.RenderEvent;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.render.Camera;

import com.google.gwt.user.client.Timer;

public abstract class AnimatedView extends View {

	public AnimatedView()
	{
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
		this.dispatch(new RenderEvent());
	}

	protected void startAnimation(Camera finalCamera) {
		animator = new AnimateCamera(getCamera().getViewMatrix(), finalCamera.getViewMatrix(), 25);
		renderTimer.scheduleRepeating(30);
	}
	
	public void zoomToBoundingBox(Box2D boundingBox) 
	{
		Camera finalCamera = getCamera().create();
		finalCamera.zoomToBoundingBox(boundingBox);

		startAnimation(finalCamera);
	}
}
