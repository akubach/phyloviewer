package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.LayoutCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCircular;

public class ViewCircular extends DetailView {

	public ViewCircular(int width, int height) {
		super(width, height);
		
		this.setCamera(new CameraCircular());
		this.getCamera().resize(width,height);
		
		this.setRenderer(new RenderTreeCircular());
		this.setLayout(new LayoutCircular(0.5));
		
		this.setPannable(true, true);
	}

}
