package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.math.Matrix33;
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

				// Calculate the maximum height using 15 pixels per leaf.
				// TODO: Need to make 15 an option.
				int maximumHeight = numberOfNodes * 15;

				int width = getWidth();
				int height = Math.max(maximumHeight, getHeight());

				this.resize(width, height);
			}
		}
	}

	public void setViewableArea(int x, int y, int width, int height)
	{
		Camera camera = getCamera();
		if ( camera != null )
		{
			int canvasHeight = getHeight();
			int canvasWidth = getWidth();
			double left = (double) x / (double) canvasWidth;
			double bottom = (double) y / (double) canvasHeight;
			
			Matrix33 matrix = Matrix33.makeTranslate( left, bottom);
			camera.setViewMatrix(matrix);
		}
	}
}
