package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.shared.render.CameraCircular;
import org.iplantc.phyloviewer.shared.render.RenderTreeCircular;

public class ViewCircular extends DetailView
{
	public ViewCircular(int width, int height)
	{
		super(width, height);

		this.setCamera(new CameraCircular());

		this.setRenderer(new RenderTreeCircular());
		this.setLayoutType(View.LayoutType.LAYOUT_TYPE_CIRCULAR);
	}
}
