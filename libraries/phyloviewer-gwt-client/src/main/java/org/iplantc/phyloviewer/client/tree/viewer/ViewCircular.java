package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCircular;

import com.google.gwt.event.shared.EventBus;

public class ViewCircular extends DetailView {

	public ViewCircular(int width, int height,SearchServiceAsyncImpl searchService,EventBus eventBus) {
		super(width, height,searchService,eventBus);
		
		this.setCamera(new CameraCircular());
		this.getCamera().resize(width,height);
		
		this.setRenderer(new RenderTreeCircular());
		this.setLayout(new RemoteLayout());
		this.setLayoutType(View.LayoutType.LAYOUT_TYPE_CIRCULAR);
		
		this.setPannable(true, true);
	}
}
