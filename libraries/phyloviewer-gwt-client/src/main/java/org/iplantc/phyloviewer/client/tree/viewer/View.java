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
import org.iplantc.phyloviewer.client.tree.viewer.render.Camera;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FocusPanel;

public abstract class View extends FocusPanel {

	public enum LayoutType {
		LAYOUT_TYPE_CLADOGRAM,
		LAYOUT_TYPE_CIRCULAR
	}
	
	private Camera camera;
	private IDocument document;
	private ILayout layout;
	private List<NodeClickedHandler> nodeClickedHandlers = new ArrayList<NodeClickedHandler>();
	private boolean renderRequestPending = false;
	LayoutType layoutType;
	EventBus eventBus;
	
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
	
	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
		
		if (layout != null && document != null && document.getTree() != null)
		{
			layout.init(getTree().getNumberOfNodes());
		}
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
		return layout;
	}

	public void setLayout(ILayout layout) {
		this.layout = layout;
		ITree tree = this.getTree();
		if (layout != null && tree != null)
		{
			layout.init(tree.getNumberOfNodes());
		}
		//doLayout();
	}
	
	public void addNodeClickedHandler(NodeClickedHandler handler) {
		if(handler!=null) {
			nodeClickedHandlers.add(handler);
		}
	}
	
	public void zoomToFit() {
		if ( null != this.getCamera() && null != this.getLayout() && null != this.getTree() ) {
			getCamera().zoomToFitSubtree(getTree().getRootNode(),getLayout());
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
					View.this.render();
					View.this.renderRequestPending = false;
				}
			});
		}
	}
	
	public abstract String exportImageURL();

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
	
	protected void pan(double xAmount,double yAmount) {
		getCamera().pan(xAmount, yAmount);
		dispatch(EventFactory.createRenderEvent());
	}
	
	protected void zoom(double amount) {
		getCamera().zoom(amount);
		dispatch(EventFactory.createRenderEvent());
	}
}
