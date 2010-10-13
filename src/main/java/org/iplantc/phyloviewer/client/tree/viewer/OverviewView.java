/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.services.TreeImage;
import org.iplantc.phyloviewer.client.services.TreeImageAsync;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.Image;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.ImageListener;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.IntersectTree;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.Defaults;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OverviewView extends View {

	private static final String MESSAGE_ERROR_LOADING_IMAGE = "Error loading image.";
	private static final String MESSAGE_LOADING_IMAGE = "Loading image...";

	private final class ImageListenerImpl implements ImageListener {
		private OverviewView view=null;
		public ImageListenerImpl(OverviewView view) {
			this.view=view;
		}
		public void onLoadingComplete(Image image) {
			view.image = downloadingImage;
			view.downloadingImage = null;
			view.imageStatus = ImageStatus.IMAGE_STATUS_IMAGE_LOADED;
			view.requestRender();
		}
	}
	
	enum ImageStatus
	{
		IMAGE_STATUS_NO_TREE,
		IMAGE_STATUS_IMAGE_LOADED,
		IMAGE_STATUS_LOADING_IMAGE,
		IMAGE_STATUS_ERROR
	}

	private Canvas canvas = null;
	private Image image = null;
	private Image downloadingImage = null;
	private int width;
	private int height;
	private ImageStatus imageStatus = ImageStatus.IMAGE_STATUS_NO_TREE;
	private INode hit;
	private TreeImageAsync treeImageService = GWT.create(TreeImage.class);
	private View detailView;
	
	public OverviewView(int width,int height, View detailView) {
		this.width = width;
		this.height = height;
		this.detailView = detailView;
		
		canvas = new Canvas(width,height);
		
		this.add(canvas);
		
		this.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();

				// Project the point in screen space to object space.
				Vector2 position = new Vector2 ( (double) x / OverviewView.this.width, (double) y / OverviewView.this.height );
				
				//TODO: The intersection right now only intersects using the layout information that is loaded on the client side.
				// Should this be moved to the server?
				IntersectTree intersector = new IntersectTree(OverviewView.this.getTree(),position, getLayout());
				intersector.intersect();
				INode hit = intersector.hit();
				OverviewView.this.hit = hit;
				OverviewView.this.requestRender();
			}
			
		});
		
		this.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent arg0) {
				notifyNodeClicked(hit);
			}
		});
	}
	
	@Override
	public ITree getTree() {
		return detailView.getTree();
	}
	
	@Override
	public void setTree(ITree tree) {
		this.image = null;
		this.downloadingImage = null;
		
		this.requestRender();
	}

	@Override
	public ILayout getLayout() {
		return detailView.getLayout();
	}

	@Override
	public void setLayout(ILayout layout) {
		//do nothing
	}
	
	public void updateImage() {
		this.retrieveOverviewImage();
	}

	private void retrieveOverviewImage() {
		this.downloadingImage = null;
		
		if (this.getTree() == null) {
			this.image = null;
			return;
		}
		
		this.imageStatus = ImageStatus.IMAGE_STATUS_LOADING_IMAGE;
		
		final AsyncCallback<String> callback = new AsyncCallback<String>()
		{
			final OverviewView caller = OverviewView.this;
			
			@Override
			public void onFailure(Throwable arg0) 
			{
				image = null;
				downloadingImage = null;
				caller.imageStatus = ImageStatus.IMAGE_STATUS_ERROR;
				
				GWT.log("Failure retrieving overview image.", arg0);
			}

			@Override
			public void onSuccess(String result) 
			{
				downloadingImage = new Image(result, new ImageListenerImpl(caller));
				caller.requestRender();
			}					
		};
		
		if (this.getTree() != null && this.getTree().getRootNode() instanceof RemoteNode) {
			final ITree tree = this.getTree();
			final RenderTree renderer = new RenderTreeCladogram(); //TODO make this use the same renderer as the view it is an overview of
			renderer.setCollapseOverlaps(false);
			renderer.setDrawLabels(false);
			
			if (this.getLayout() != null && this.getLayout() instanceof RemoteLayout) {
				RemoteLayout remoteLayout = (RemoteLayout)this.getLayout();
				
				//It's likely retrieveOverviewImage was called before the detailView's layout RPC call has returned.  Doing the image fetch when the layout is done, in a layout callback:
				remoteLayout.layoutAsync(tree, remoteLayout.new DidLayout() {
					//@Override
					protected void didLayout(String layoutID) {
						treeImageService.getTreeImageURL(tree.getId(), layoutID, renderer, width, height, callback);
					}
				});
			}
		} else {
			imageStatus = ImageStatus.IMAGE_STATUS_ERROR;
		}
		
	}

	public void render() {
		canvas.clear();
		
		if (image!=null && image.isLoaded() ) {
			canvas.save();
			
			canvas.scale((double)width/image.getWidth(), (double)height/image.getHeight());
			canvas.drawImage(image, 0, 0);
			
			canvas.restore();
		}
		else {
			switch(imageStatus) {
			case IMAGE_STATUS_LOADING_IMAGE:
				showStatusMessage(OverviewView.MESSAGE_LOADING_IMAGE);
				break;
			case IMAGE_STATUS_ERROR:
				showStatusMessage(OverviewView.MESSAGE_ERROR_LOADING_IMAGE);
			}
		}

		Camera camera = this.getCamera();
		if(camera!=null) {
			Matrix33 V=camera.getViewMatrix();
			Matrix33 IV=V.inverse();
			Vector2 min=new Vector2(0,0);
			Vector2 max=new Vector2(1,1);
			
			min=IV.transform(min);
			max=IV.transform(max);
			
			Matrix33 S = Matrix33.makeScale(canvas.getWidth(),canvas.getHeight());
			min=S.transform(min);
			max=S.transform(max);
			
			double x = min.getX();
			double y = min.getY();
			double width=max.getX()-x;
			double height=max.getY()-y;
			
			canvas.setStrokeStyle(Defaults.OVERVIEW_OUTLINE_COLOR);
			canvas.setFillStyle(Defaults.OVERVIEW_FILL_COLOR);
			canvas.beginPath();
			canvas.rect(x,y,width,height);
			canvas.fill();
			canvas.stroke();
		}
		
		if(hit != null) {
			canvas.setFillStyle("red");
			canvas.beginPath();
			
			ILayout layout = this.getLayout();
			canvas.arc(layout.getPosition(hit).getX() * this.width, layout.getPosition(hit).getY() * this.height, Defaults.POINT_RADIUS, 0, Math.PI*2, true); 
			canvas.closePath();
			canvas.fill();
		}
	}

	private void showStatusMessage(String message) {
		canvas.setStrokeStyle(Defaults.TEXT_COLOR);
		canvas.setFillStyle(Defaults.TEXT_COLOR);
		canvas.fillText(message, 0, canvas.getHeight() / 2);
	}

	public void resize(int width, int height) {
		this.width=width;
		this.height=height;
		canvas.setWidth(width);
		canvas.setHeight(height);
		retrieveOverviewImage(); //FIXME: limit the rate of these requests.  Most browsers are going to have many resize events as the user drags the window border around.
	}

	@Override
	public int getHeight() {
		return canvas.getHeight();
	}

	@Override
	public int getWidth() {
		return canvas.getWidth();
	}

	@Override
	public boolean isReady() {
		return true;
	}
}
