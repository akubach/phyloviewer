package org.iplantc.iptol.client.tree.viewer;

import org.iplantc.iptol.client.services.TreeServices;
import org.iplantc.iptol.client.tree.viewer.canvas.Canvas;
import org.iplantc.iptol.client.tree.viewer.canvas.Image;
import org.iplantc.iptol.client.tree.viewer.canvas.ImageListener;
import org.iplantc.iptol.client.tree.viewer.math.Matrix33;
import org.iplantc.iptol.client.tree.viewer.math.Vector2;
import org.iplantc.iptol.client.tree.viewer.render.Camera;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;

public class OverviewView extends FocusPanel {

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

	private Canvas canvas = null;
	private Image image = null;
	private Camera camera = null;
	private int width;
	private int height;
	
	public OverviewView(int width,int height) {
		this.width = width;
		this.height = height;
		
		canvas = new Canvas(width,height);
		this.add(canvas);		
	}
	
	public void loadFromJSON(String json) {
				
		final OverviewView caller = this;
		
		TreeServices.getTreeImage(json,width,height,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//TODO: handle failure					
			}

			@Override
			public void onSuccess(String result) 
			{
				image = new Image(result, new ImageListenerImpl(caller));				
			}					
		});				
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void render() {
		canvas.clear();
		
		if (image!=null) {
			canvas.drawImage(image, 0, 0);
		}
		
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
			
			canvas.beginPath();
			canvas.rect(x,y,width,height);
			canvas.stroke();
		}
	}
}
