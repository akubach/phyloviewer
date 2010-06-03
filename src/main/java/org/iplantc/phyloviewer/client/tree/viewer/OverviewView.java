package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.services.TreeImageServices;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.Image;
import org.iplantc.phyloviewer.client.tree.viewer.canvas.ImageListener;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.Defaults;
import org.iplantc.phyloviewer.client.tree.viewer.render.IntersectTree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
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
			if(view!=null){
				view.render();
			}
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
	private int width;
	private int height;
	private String json;
	private ImageStatus imageStatus = ImageStatus.IMAGE_STATUS_NO_TREE;
	private INode hit;
	
	public OverviewView(int width,int height) {
		this.width = width;
		this.height = height;
		
		canvas = new Canvas(width,height);
		
		this.add(canvas);
		
		this.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();

				// Project the point in screen space to object space.
				Vector2 position = new Vector2 ( (double) x / OverviewView.this.width, (double) y / OverviewView.this.height );
				
				IntersectTree intersector = new IntersectTree(OverviewView.this.getTree(),position);
				intersector.intersect();
				INode hit = intersector.hit();
				OverviewView.this.hit = hit;
				
				DeferredCommand.addCommand(new Command() {

					@Override
					public void execute() {
						OverviewView.this.render();
					}
				});
			}
			
		});
		
		this.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent arg0) {
				notifyNodeClicked(hit);
			}
		});
	}
	
	public void loadFromJSON(String json) {
		this.json = json;
		retriveOverviewImage();
	}

	private void retriveOverviewImage() {
		
		if ( null == this.json || this.json.isEmpty() )
			return;
		
		this.image = null;
		this.imageStatus = ImageStatus.IMAGE_STATUS_LOADING_IMAGE;
		
		final OverviewView caller = this;
		TreeImageServices.getTreeImage(this.json,width,height,false,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				caller.imageStatus = ImageStatus.IMAGE_STATUS_ERROR;
				
				GWT.log("Failure retrieving overview image.", arg0);
			}

			@Override
			public void onSuccess(String result) 
			{
				caller.imageStatus = ImageStatus.IMAGE_STATUS_IMAGE_LOADED;
				
				image = new Image(result, new ImageListenerImpl(caller));
			}					
		});
	}

	public void render() {
		canvas.clear();
		
		if (image!=null) {
			canvas.drawImage(image, 0, 0);
		}
		
		switch(imageStatus) {
		case IMAGE_STATUS_LOADING_IMAGE:
			showStatusMessage(OverviewView.MESSAGE_LOADING_IMAGE);
			break;
		case IMAGE_STATUS_ERROR:
			showStatusMessage(OverviewView.MESSAGE_ERROR_LOADING_IMAGE);
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
			canvas.arc(hit.getPosition().getX() * this.width, hit.getPosition().getY() * this.height, Defaults.POINT_RADIUS, 0, Math.PI*2, true); 
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
		retriveOverviewImage();
	}
}
