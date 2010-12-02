package org.iplantc.phyloviewer.client.tree.viewer;

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
		
		render();
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
}
