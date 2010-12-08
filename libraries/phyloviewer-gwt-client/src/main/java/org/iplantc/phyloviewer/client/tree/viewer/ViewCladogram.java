package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * The ViewCladogram class is a composite of the overview and detail views.
 * @author adamk
 *
 */

public class ViewCladogram extends View {

	private DetailView detailView;
	private OverviewView overviewView;
	
	public ViewCladogram(int width, int height,SearchServiceAsyncImpl searchService) {
		
		detailView = new DetailView(1,1,searchService);
		overviewView = new OverviewView(1,1, detailView);
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(overviewView);
		panel.add(detailView);
		this.add(panel);
		
		Camera camera = new CameraCladogram();
		
		this.setCamera(camera);
		
		overviewView.setCamera(camera);
		detailView.setCamera(camera);
		
		detailView.setPannable(false, true);
		
		this.zoomToFit();
		
		this.setLayout(new RemoteLayout(ILayout.LayoutType.LAYOUT_TYPE_CLADOGRAM));
		
		this.resize(width, height);
	}
	
	public void resize(int width, int height) {
		int overviewWidth=(int) (width*0.20);
		int detailWidth = width-overviewWidth;
		
		overviewView.resize(overviewWidth,height);
		detailView.resize(detailWidth,height);
	}
	
	public final void setLayout(ILayout layout) {
		detailView.setLayout(layout);
	}
	
	public final void render() {
		overviewView.render();
		detailView.render();
	}
	
	/**
	 *  Set the tree.  Make sure both views get the tree.
	 */
	public final void setDocument(IDocument document) {
		super.setDocument(document);
		detailView.setDocument(document);
		overviewView.setDocument(document);
		
		overviewView.updateImage();
	}
	
	@Override
	public ITree getTree() {
		return detailView.getTree();
	}
	
	public final int getWidth() {
		return overviewView.getWidth() + detailView.getWidth();
	}
	
	public final int getHeight() {
		// Both the overview and detail views should have the same height.
		return detailView.getHeight(); 
	}
	
	public final void addNodeClickedHandler(NodeClickedHandler handler) {
		super.addNodeClickedHandler(handler);
		overviewView.addNodeClickedHandler(handler);
		detailView.addNodeClickedHandler(handler);
	}

	@Override
	public boolean isReady() {
		return overviewView.isReady() && detailView.isReady();
	}
	
	@Override 
	public ILayout getLayout() {
		return detailView.getLayout();
	}

	@Override
	public String exportImageURL()
	{
		return detailView.exportImageURL();
	}

	@Override
	public void setRenderPreferences(RenderPreferences preferences)
	{
		detailView.setRenderPreferences(preferences);
		overviewView.setRenderPreferences(preferences);
	}
}
