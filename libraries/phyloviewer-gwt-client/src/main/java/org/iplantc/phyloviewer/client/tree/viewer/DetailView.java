/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.EventFactory;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.SearchHighlighter;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;

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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;

public class DetailView extends View implements HasDoubleClickHandlers {
	private int renderCount;
	private double[] renderTime = new double[60];
	private boolean debug = true;

	private Graphics graphics = null;
	private RenderTree renderer = new RenderTreeCladogram();
	private SearchHighlighter highlighter = null;
	private Vector2 clickedPosition = null;
	private Vector2 event0 = null;
	private Vector2 event1 = null;
	
	private boolean panX = false;
	private boolean panY = true;
	
	private final RequestRenderCallback renderCallback = new RequestRenderCallback();
	
	private SearchServiceAsyncImpl searchService;
	
	public DetailView(int width,int height,SearchServiceAsyncImpl searchService,EventBus eventBus) {
		super(eventBus);
		
		this.searchService = searchService;
		
		this.setCamera(new CameraCladogram());
		
		graphics = new Graphics(width,height);
		
		this.add(graphics.getWidget());
		
		this.addMouseWheelHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent event) {
				double amount = -event.getDeltaY()/10.0;
				amount = Math.pow(2, amount);
				zoom(amount);
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
						 
						 Matrix33 M = getCamera().getMatrix(getWidth(),getHeight());
						 Matrix33 IM = M.inverse();
						 
						 Vector2 p0 = IM.transform(event0);
						 Vector2 p1 = IM.transform(event1);
						 
						 double x = DetailView.this.panX ? p0.getX() - p1.getX() : 0.0;
						 double y = DetailView.this.panY ? p0.getY() - p1.getY() : 0.0;
						 pan(x, y);
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
				Matrix33 M = camera.getMatrix(getWidth(),getHeight());
				Matrix33 IM = M.inverse();
				
				// Project the point in screen space to object space.
				Vector2 position = IM.transform(new Vector2(x,y));
				
				if(renderer instanceof RenderTreeCircular) {
					Vector2 p = position.subtract(new Vector2(0.5,0.5));
					PolarVector2 polar = new PolarVector2(p);
					position.setX((polar.getRadius()*2)*0.8);
					position.setY(polar.getAngle() / (Math.PI * 2.0));
				}
				
				IntersectTree intersector = new IntersectTree(getTree(),position, getLayout());
				intersector.intersect();
				INode hit = intersector.hit();
				
				ILayout layout = getLayout();
				if(hit!=null && layout != null) {
					int nodeId = hit.getId();
					Vector2 hitPosition = layout.getPosition(hit);
					Box2D hitBounds = layout.getBoundingBox(hit);
					dispatch(EventFactory.createNodeClickedEvent(nodeId,hitPosition,hitBounds));
				}
			}
			
		});
	}

	public void render() {
		
		if(this.isReady()) {
			Duration duration = new Duration();
			renderer.renderTree(graphics, getCamera(), this.renderCallback);
			
			if (debug) {
				renderStats(duration.elapsedMillis());
			}
		}
	}

	public void resize(int width, int height) {
		graphics.resize(width,height);
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
		return graphics.getHeight();
	}

	@Override
	public int getWidth() {
		return graphics.getWidth();
	}
	
	public RenderTree getRenderer() {
		return renderer;
	}

	public void setRenderer(RenderTree renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void setDocument(IDocument document) {
		super.setDocument(document);
		this.getCamera().reset();
		
		if (highlighter != null)
		{
			highlighter.dispose();
		}
		
		ITree tree = this.getTree();
		if (tree != null && renderer != null && this.searchService != null)
		{
			highlighter = new SearchHighlighter(this, this.searchService, tree, renderer.getRenderPreferences());
		}
		
		if(renderer != null) {
			renderer.setDocument(document);
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
		
		String text = renderCount + " frames, last: " + Math.round( 1.0 / time * 1000 ) + " FPS";
		
		if (renderCount >= 60) {
			double totalTime = 0;
			
			for(double t:renderTime) {
				totalTime += t;
			}
			double fps = ( 60.0 / totalTime ) * 1000;
			
			text += " average: " +  Math.round(fps) + " FPS";
		}
		
		graphics.getCanvas().setFillStyle("red");
		graphics.getCanvas().fillText(text, 5, graphics.getCanvas().getHeight() - 5);
	}
	
	public String exportImageURL()
	{
		return graphics.getCanvas().toDataURL();
	}
	
	public void setRenderPreferences(RenderPreferences preferences)
	{
		renderer.setRenderPreferences(preferences);
	}
}
