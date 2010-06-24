package org.iplantc.phyloviewer.client.tree.viewer;


import org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.IGraphics;
import org.iplantc.phyloviewer.client.tree.viewer.render.ILayoutCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.IntersectTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;

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

	private Canvas canvas = null;
	private IGraphics graphics = null;
	
	private Vector2 clickedPosition = null;
	private Vector2 event0 = null;
	private Vector2 event1 = null;
	
	private boolean panX = false;
	private boolean panY = true;
	
	public DetailView(int width,int height) {
		
		this.setCamera(new Camera());
		
		canvas = new Canvas(width,height);
		graphics = new Graphics(canvas);
		
		this.getCamera().resize(width,height);
		
		this.add(canvas);
		
		this.addMouseWheelHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent event) {
				getCamera().zoomInYDirection(event.getDeltaY()/10.0);
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
		if (this.getLayout() instanceof ILayoutCircular) {
			RenderTreeCircular.renderTree(this.getTree(), (ILayoutCircular)this.getLayout(), graphics, this.getCamera());
		} else {
			RenderTree.renderTree(this.getTree(),this.getLayout(),graphics,this.getCamera());
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
}
