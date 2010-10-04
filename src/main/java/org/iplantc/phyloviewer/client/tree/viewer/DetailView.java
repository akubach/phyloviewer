/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;


import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.layout.IntersectTree;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.IStyleMap;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.StyleMap;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class DetailView extends View implements HasDoubleClickHandlers {
	private int renderCount;
	private double[] renderTime = new double[60];
	private boolean debug = true;

	private Canvas canvas = null;
	private IGraphics graphics = null;
	private RenderTree renderer = new RenderTreeCladogram();
	private Vector2 clickedPosition = null;
	private Vector2 event0 = null;
	private Vector2 event1 = null;
	
	private boolean panX = false;
	private boolean panY = true;
	
	private IStyleMap styleMap = new StyleMap();
	private final RequestRenderCallback renderCallback = new RequestRenderCallback();
	
	public DetailView(int width,int height) {
		//TODO consider moving all of the styling stuff out of DetailView and out of INode and into the renderer
		RemoteNode.setStyleMap(this.styleMap);
		
		this.setCamera(new CameraCladogram());
		
		canvas = new Canvas(width,height);
		graphics = new Graphics(canvas);
		
		this.getCamera().resize(width,height);
		
		this.add(canvas);
		
		this.addMouseWheelHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent event) {
				double amount = -event.getDeltaY()/10.0;
				amount = Math.pow(2, amount);
				getCamera().zoom(amount);
			}
		});
		
		this.addMouseDownHandler(new MouseDownHandler(){
			public void onMouseDown(MouseDownEvent event) {
				clickedPosition = new Vector2(event.getX(), event.getY());
			}
		});
		
		this.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				
				event1 = event0;
				event0 = new Vector2 ( event.getX(),event.getY() );
				
				 if ( NativeEvent.BUTTON_LEFT == event.getNativeButton() ) {
					 if ( event0 != null && event1 != null && clickedPosition != null ) {
						 
						 Matrix33 M = getCamera().getMatrix();
						 Matrix33 IM = M.inverse();
						 
						 Vector2 p0 = IM.transform(event0);
						 Vector2 p1 = IM.transform(event1);
						 
						 double x = DetailView.this.panX ? p0.getX() - p1.getX() : 0.0;
						 double y = DetailView.this.panY ? p0.getY() - p1.getY() : 0.0;
						 getCamera().pan(x, y);
					 }
				 }
			}
		});
		
		this.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				clickedPosition = null;
				event0 = null;
				event1 = null;
			}
		});
		
		// Add double click handler.
		// See: http://techblog.traveltripper.com/2009/10/catching-double-clicks-in-gwt.html
		this.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent arg0) {
				
				int x = arg0.getNativeEvent().getClientX() - DetailView.this.getElement().getAbsoluteLeft();
				int y = arg0.getNativeEvent().getClientY() - DetailView.this.getElement().getAbsoluteTop();
				
				Camera camera = getCamera();
				Matrix33 M = camera.getMatrix();
				Matrix33 IM = M.inverse();
				
				// Project the point in screen space to object space.
				Vector2 position = IM.transform(new Vector2(x,y));
				
				IntersectTree intersector = new IntersectTree(getTree(),position, getLayout());
				intersector.intersect();
				INode hit = intersector.hit();
				
				notifyNodeClicked(hit);				
			}
			
		});
	}

	public void render() {
		
		Duration duration = new Duration();
		renderer.renderTree(this.getTree(), this.getLayout(), graphics, getCamera(), this.renderCallback);
		
		if (debug) {
			renderStats(duration.elapsedMillis());
		}
	}

	public void resize(int width, int height) {
		canvas.setWidth(width);
		canvas.setHeight(height);
		this.getCamera().resize(width, height);
	}

	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return addDomHandler(handler, DoubleClickEvent.getType());
	}
	
	public void setPannable(boolean x, boolean y) {
		this.panX = x;
		this.panY = y;
	}
	
	@Override
	public int getHeight() {
		return canvas.getHeight();
	}

	@Override
	public int getWidth() {
		return canvas.getWidth();
	}
	
	public RenderTree getRenderer() {
		return renderer;
	}

	public void setRenderer(RenderTree renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void setTree(ITree tree) {
		super.setTree(tree);
		if (tree != null) {
			styleMap.styleSubtree(tree.getRootNode());
		}
	}

	@Override
	public boolean isReady() {
		boolean ready = this.getTree() != null && this.getLayout() != null && graphics != null && getCamera() != null;
		
		if (ready && this.getLayout() instanceof RemoteLayout) {
			ready &= ((RemoteLayout)this.getLayout()).containsNode(this.getTree().getRootNode());
		}
		
		return ready;
	}
	
	public class RequestRenderCallback {		
		private RequestRenderCallback() {}

		public void requestRender() {
			DetailView.this.requestRender();
		}
	}
	
	private void renderStats(double time) {
		renderCount++;
		int index = renderCount % 60;
		renderTime[index] = time;
		
		String text = renderCount + " frames, last: " + 1.0 / time * 1000 + " FPS";
		
		if (renderCount >= 60) {
			double totalTime = 0;
			
			for(double t:renderTime) {
				totalTime += t;
			}
			double fps = ( 60.0 / totalTime ) * 1000;
			
			text += " average: " + fps + " FPS";
		}
		
		canvas.setFillStyle("red");
		canvas.fillText(text, 5, canvas.getHeight() - 5);
	}
}
