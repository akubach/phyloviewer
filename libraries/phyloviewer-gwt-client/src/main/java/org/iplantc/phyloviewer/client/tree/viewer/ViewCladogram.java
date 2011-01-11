package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.NodeClickedHandler;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * The ViewCladogram class is a composite of the overview and detail views.
 * @author adamk
 *
 */

public class ViewCladogram extends AnimatedView {

	private DetailView detailView;
	private OverviewView overviewView;
	
	public ViewCladogram(int width, int height, SearchServiceAsyncImpl searchService, EventBus eventBus) {
		super(eventBus);
		detailView = new DetailView(1, 1, searchService, eventBus);
		overviewView = new OverviewView(1, 1, detailView, eventBus);
		
		//refire events from the sub-views, with this view as source
		detailView.addSelectionHandler(refireHandler);
		overviewView.addSelectionHandler(refireHandler);
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(overviewView);
		panel.add(detailView);
		this.add(panel);
		
		Camera camera = new CameraCladogram();
		
		this.setCamera(camera);
		
		overviewView.setCamera(camera);
		detailView.setCamera(camera);
		
		this.setLayoutType(View.LayoutType.LAYOUT_TYPE_CLADOGRAM);
		overviewView.setLayoutType(View.LayoutType.LAYOUT_TYPE_CLADOGRAM);
		detailView.setLayoutType(View.LayoutType.LAYOUT_TYPE_CLADOGRAM);
		
		detailView.setPannable(false, true);
		
		this.zoomToFit();
		
		this.resize(width, height);
		
		this.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent arg0) {
				final char charCode = arg0.getCharCode();
				if ( charCode == 'h' ) {
					overviewView.setVisible(!overviewView.isVisible());
				}
			}
		});
	}
	
	public void resize(int width, int height) {
		int overviewWidth=(int) (width*0.20);
		int detailWidth = width-overviewWidth;
		
		overviewView.resize(overviewWidth,height);
		detailView.resize(detailWidth,height);
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
