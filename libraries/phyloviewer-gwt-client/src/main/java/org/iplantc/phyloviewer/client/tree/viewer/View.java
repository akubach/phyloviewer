/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.events.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.events.HasDocument;
import org.iplantc.phyloviewer.client.events.HasNodeSelectionHandlers;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.events.RenderEvent;
import org.iplantc.phyloviewer.client.tree.viewer.render.HasRenderPreferences;
import org.iplantc.phyloviewer.client.events.RenderHandler;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public abstract class View extends FocusPanel implements RequiresResize, HasDocument, HasNodeSelectionHandlers, HasRenderPreferences {

	public enum LayoutType {
		LAYOUT_TYPE_CLADOGRAM,
		LAYOUT_TYPE_CIRCULAR
	}
	
	private Camera camera;
	private IDocument document;
	private boolean renderRequestPending = false;
	LayoutType layoutType;
	private EventBus eventBus = null;
	private RenderPreferences renderPreferences = new RenderPreferences();
	
	/** A NodeSelectionHandler that re-fires selection events with this view as the source */
	protected NodeSelectionHandler refireHandler = new NodeSelectionHandler()
	{
		@Override
		public void onNodeSelection(NodeSelectionEvent event)
		{
			getEventBus().fireEventFromSource(new NodeSelectionEvent(event.getSelectedNodes()), View.this);
		}
	};
		
	public View() {
		this.initEventListeners();
	}
	
	@Override
	public IDocument getDocument() {
		return document;
	}

	@Override
	public void setDocument(IDocument document) {
		this.document = document;
		
		eventBus.fireEventFromSource(new DocumentChangeEvent(document), this);
	}
	
	@Override
	public HandlerRegistration addDocumentChangeHandler(DocumentChangeHandler handler)
	{
		return eventBus.addHandlerToSource(DocumentChangeEvent.TYPE, this, handler);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(NodeSelectionHandler handler)
	{
		return eventBus.addHandlerToSource(NodeSelectionEvent.TYPE, this, handler);
	}

	public ITree getTree() {
		return this.getDocument() != null ? this.getDocument().getTree() : null;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public ILayoutData getLayout() {
		return document != null ? document.getLayout() : null;
	}
	
	public void zoomToFit() 
	{
		if (null != this.getTree()) 
		{
			zoomToFitSubtree(getTree().getRootNode());
		}
	}
	
	public void zoomToFitSubtree(final INode subtree) 
	{
		if(subtree != null) {
			this.zoomToFitSubtree(subtree.getId());
		}
	}
	
	public void zoomToFitSubtree(final int nodeId) 
	{
		ILayoutData layout = this.getLayout();
		if (null != layout) 
		{
			// No need to check for layout data.  As of now, layout data always is present with node data.
			// If this changes, logic for handling remote data should be in the document.
			Box2D boundingBox = layout.getBoundingBox(nodeId);
			this.zoomToBoundingBox(boundingBox);
		}
	}
	
	public void zoomToBoundingBox(Box2D boundingBox) 
	{
		if (null != this.getCamera()) 
		{
			getCamera().zoomToBoundingBox(boundingBox);
			this.dispatch(new RenderEvent());
		}
	}
	
	public abstract void resize(int width, int height);
	public abstract void render(); 

	public abstract int getWidth();
	public abstract int getHeight();
	
	/** 
	 * This gets called by TreeWidget before every render, so it must return quickly 
	 */
	public abstract boolean isReady();

	public void requestRender() {
		
		if(!this.renderRequestPending) {
			this.renderRequestPending = true;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					if (View.this.isReady())
					{
						View.this.render();
						View.this.renderRequestPending = false;
					} 
					else
					{
						Scheduler.get().scheduleDeferred(this);
					}
				}
			});
		}
	}
	
	public abstract String exportImageURL();
	
	public void setEventBus(EventBus eventBus)
	{
		this.eventBus = eventBus;
		this.initEventListeners();
	}
	
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public void onResize()
	{
		resize(getParent().getOffsetWidth(), getParent().getOffsetHeight());
		this.requestRender();
	}

	@Override
	public RenderPreferences getRenderPreferences()
	{
		return renderPreferences;
	}
	
	@Override
	public void setRenderPreferences(RenderPreferences rp) 
	{
		this.renderPreferences = rp;
	}
	
	public LayoutType getLayoutType() {
		return layoutType;
	}
	
	protected void setLayoutType(LayoutType type) {
		this.layoutType = type;
	}
	
	protected void dispatch(GwtEvent<?> event)
    {
		if(eventBus != null) {
			eventBus.fireEventFromSource(event,this);
		}
    }
	
	protected void initEventListeners()
    {
		if(eventBus != null) {
			eventBus.addHandler(RenderEvent.TYPE, new RenderHandler()
	        {
	            @Override
	            public void onRender(RenderEvent event)
	            {
	            	requestRender();
	            }
	        });
		}
    }
	
	public void pan(double xAmount,double yAmount) {
		getCamera().pan(xAmount, yAmount);
		dispatch(new RenderEvent());
	}
	
	public void zoom(double amount) {
		getCamera().zoom(amount);
		dispatch(new RenderEvent());
	}
}
