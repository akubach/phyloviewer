package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.LayoutCladogramHashMap;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCladogram;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * The ViewCladogram class is a composite of the overview and detail views.
 * @author adamk
 *
 */

public class ViewCladogram extends View {

	private OverviewView overviewView = new OverviewView(1,1);
	private DetailView detailView = new DetailView(1,1);
	
	public ViewCladogram(int width, int height) {
		
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
		
		this.setLayout(new RemoteLayout(new LayoutCladogramHashMap(0.8, 1.0)));
		
		this.resize(width, height);
	}
	
	public void resize(int width, int height) {
		int overviewWidth=(int) (width*0.20);
		int detailWidth = width-overviewWidth;
		
		overviewView.resize(overviewWidth,height);
		detailView.resize(detailWidth,height);
	}
	
	public final void setLayout(ILayout layout) {
		overviewView.setLayout(layout);
		detailView.setLayout(layout);
	}
	
	public final void render() {
		overviewView.render();
		detailView.render();
	}
	
	/**
	 *  Set the tree.  Make sure both views get the tree.
	 */
	public final void setTree(ITree tree) {
		overviewView.setTree(tree);
		detailView.setTree(tree);
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
}
