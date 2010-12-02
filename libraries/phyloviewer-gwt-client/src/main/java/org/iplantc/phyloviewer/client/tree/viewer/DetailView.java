/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.SearchHighlighter;
import org.iplantc.phyloviewer.client.tree.viewer.render.canvas.Graphics;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;

import com.google.gwt.core.client.Duration;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class DetailView extends View implements HasDoubleClickHandlers {
	private int renderCount;
	private double[] renderTime = new double[60];
	private boolean debug = true;

	private Graphics graphics = null;
	private RenderTree renderer = new RenderTreeCladogram();
	private SearchHighlighter highlighter = null;
	
	private boolean panX = false;
	private boolean panY = true;
	
	private final RequestRenderCallback renderCallback = new RequestRenderCallback();
	
	private SearchServiceAsyncImpl searchService;
	
	private Map<EventHandler, List<HandlerRegistration>> handlerRegistrations = new HashMap<EventHandler, List<HandlerRegistration>>();
	
	public DetailView(int width,int height,SearchServiceAsyncImpl searchService,EventBus eventBus) {
		super(eventBus);
		
		this.searchService = searchService;
		
		this.setCamera(new CameraCladogram());
		
		graphics = new Graphics(width,height);
		
		this.add(graphics.getWidget());
		
		HandlesAllMouseEvents handler = new NavigationMouseHandler(this);
		this.addMouseHandler(handler);
	}

	public void render() {
		
		if(this.isReady()) {
			Duration duration = new Duration();
			renderer.renderTree(this.getLayout(), graphics, getCamera(), this.renderCallback);
			
			if (debug) {
				renderStats(duration.elapsedMillis());
			}
		}
	}

	public void resize(int width, int height) {
		graphics.resize(width,height);
	}
	
	public void setPannable(boolean x, boolean y) {
		this.panX = x;
		this.panY = y;
	}
	
	public boolean isXPannable()
	{
		return this.panX;
	}
	
	public boolean isYPannable()
	{
		return this.panY;
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

	/**
	 * @return the node at the given location, in this View's screen coordinate system
	 */
	public INode getNodeAt(int x, int y) 
	{
		Camera camera = getCamera();
		Matrix33 M = camera.getMatrix(getWidth(), getHeight());
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
		return hit;
	}

	private void addMouseHandler(HandlesAllMouseEvents handler) 
	{
		List<HandlerRegistration> registrations = handlerRegistrations.get(handler);
		if (registrations == null)
		{
			registrations = new ArrayList<HandlerRegistration>();
			handlerRegistrations.put(handler, registrations);
		}
		
		//add this handler for all supported events
		registrations.add(this.addMouseDownHandler(handler));
		registrations.add(this.addMouseUpHandler(handler));
		registrations.add(this.addMouseOutHandler(handler));
		registrations.add(this.addMouseOverHandler(handler));
		registrations.add(this.addMouseMoveHandler(handler));
		registrations.add(this.addMouseWheelHandler(handler));
		
		if (handler instanceof ClickHandler)
		{
			registrations.add(this.addClickHandler((ClickHandler)handler));
		}
		
		if (handler instanceof DoubleClickHandler)
		{
			registrations.add(this.addDoubleClickHandler((DoubleClickHandler)handler));
		}
	}
	
	/**
	 * Removes all handler registrations for the given handler
	 */
	private void removeMouseHandler(HandlesAllMouseEvents handler)
	{
		List<HandlerRegistration> registrations = handlerRegistrations.get(handler);
		if (registrations != null) 
		{
			for (HandlerRegistration registration : registrations)
			{
				registration.removeHandler();
			}
		}
		
		registrations.clear();
	}
}
