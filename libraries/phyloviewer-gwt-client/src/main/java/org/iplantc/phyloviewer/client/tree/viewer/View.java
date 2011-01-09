/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.events.DataPayloadEvent;
import org.iplantc.phyloviewer.client.events.DataPayloadEventHandler;
import org.iplantc.phyloviewer.client.events.EventFactory;
import org.iplantc.phyloviewer.client.events.MessagePayloadEvent;
import org.iplantc.phyloviewer.client.events.Messages;
import org.iplantc.phyloviewer.client.tree.viewer.event.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.tree.viewer.event.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.tree.viewer.event.HasDocument;
import org.iplantc.phyloviewer.client.tree.viewer.event.HasNodeSelectionHandlers;
import org.iplantc.phyloviewer.client.tree.viewer.event.NodeClickedHandler;
import org.iplantc.phyloviewer.client.tree.viewer.event.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.tree.viewer.event.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public abstract class View extends FocusPanel implements RequiresResize, HasDocument, HasNodeSelectionHandlers {

	public enum LayoutType {
		LAYOUT_TYPE_CLADOGRAM,
		LAYOUT_TYPE_CIRCULAR
	}
	
	private Camera camera;
	private IDocument document;
	private List<NodeClickedHandler> nodeClickedHandlers = new ArrayList<NodeClickedHandler>();
	private boolean renderRequestPending = false;
	LayoutType layoutType;
	private EventBus eventBus = new SimpleEventBus();
	
	/** A NodeSelectionHandler that re-fires selection events with this view as the source */
	protected NodeSelectionHandler refireHandler = new NodeSelectionHandler()
	{
		@Override
		public void onNodeSelection(NodeSelectionEvent event)
		{
			getEventBus().fireEventFromSource(new NodeSelectionEvent(event.getSelectedNodes()), View.this);
		}
	};
	
	private static final char KEY_LEFT = 0x25;
	private static final char KEY_UP = 0x26;
	private static final char KEY_RIGHT = 0x27;
	private static final char KEY_DOWN = 0x28;
		
	public View(EventBus eventBus) {
		this.eventBus = eventBus;
		this.initEventListeners();
		
		this.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent arg0) {
				if ( getCamera() == null ) {
					return;
				}
				
				final char charCode = arg0.getCharCode();
				if ( charCode == ' ' ) {
					getCamera().reset();
					dispatch(EventFactory.createRenderEvent());
				}
				else if ( charCode == KEY_UP ) {
					pan(0.0, 0.1);
				}
				else if ( charCode == KEY_DOWN ) {
					pan(0.0, -0.1);
				}
				else if ( charCode == KEY_LEFT ) {
					pan(0.1, 0.0);
				}
				else if ( charCode == KEY_RIGHT ) {
					pan(-0.1, 0.0);
				}
			}
		});
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

	public ILayout getLayout() {
		return document != null ? document.getLayout() : null;
	}
	
	public void addNodeClickedHandler(NodeClickedHandler handler) {
		if(handler!=null) {
			nodeClickedHandlers.add(handler);
		}
	}
	
	public void zoomToFit() 
	{
		if (null != this.getTree()) 
		{
			zoomToFitSubtree(getTree().getRootNode());
		}
	}
	
	public void zoomToFitSubtree(INode subtree) 
	{
		if (null != this.getCamera() && null != this.getLayout()) 
		{
			getCamera().zoomToFitSubtree(subtree, getLayout());
			this.dispatch(EventFactory.createRenderEvent());
		}
	}
	
	protected void notifyNodeClicked(INode node) {
		if(node!=null) {
			for(NodeClickedHandler handler : nodeClickedHandlers) {
				handler.onNodeClicked(node);
			}
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
	
	public abstract void setRenderPreferences(RenderPreferences preferences);

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

	public LayoutType getLayoutType() {
		return layoutType;
	}
	
	protected void setLayoutType(LayoutType type) {
		this.layoutType = type;
	}
	
	private void dispatch(MessagePayloadEvent<?> event)
    {
		if(eventBus != null) {
			eventBus.fireEventFromSource(event,this);
		}
    }
	
	private void initEventListeners()
    {
		eventBus.addHandler(DataPayloadEvent.TYPE, new DataPayloadEventHandler()
        {
            @Override
            public void onFire(DataPayloadEvent event)
            {
            	if(event.getMessageString().equals(Messages.MESSAGE_RENDER))
            	{
            		requestRender();
            	}
            }
        });
    }
	
	public void pan(double xAmount,double yAmount) {
		getCamera().pan(xAmount, yAmount);
		dispatch(EventFactory.createRenderEvent());
	}
	
	public void zoom(double amount) {
		getCamera().zoom(amount);
		dispatch(EventFactory.createRenderEvent());
	}
}
