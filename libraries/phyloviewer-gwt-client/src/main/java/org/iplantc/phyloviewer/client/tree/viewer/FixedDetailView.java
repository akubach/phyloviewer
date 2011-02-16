package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;

public class FixedDetailView extends DetailView
{
	public FixedDetailView(int width, int height)
	{
		super(width, height);

		getRenderer().getRenderPreferences().setCollapseOverlaps(false);

		// Lock the zoom.
		Camera camera = getCamera();
		if(camera != null)
		{
			camera.lockToZoom(1, 1);
		}
	}

	@Override
	public void setDocument(IDocument document)
	{
		super.setDocument(document);

		if(document != null)
		{
			ITree tree = document.getTree();
			if(tree != null)
			{
				int numberOfNodes = tree.getNumberOfNodes();

				// Calculate the maximum height.
				// 3 gave a good spacing between leaf nodes.
				// TODO: This needs to be revisited...I expected a bigger number to be better.
				int maximumHeight = numberOfNodes * 3;

				int width = getWidth();
				int height = Math.max(maximumHeight, getHeight());

				this.resize(width, height);
			}
		}
	}
}
