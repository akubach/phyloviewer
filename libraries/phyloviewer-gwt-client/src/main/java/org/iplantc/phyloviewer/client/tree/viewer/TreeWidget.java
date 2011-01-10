/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.DataPayloadEvent;
import org.iplantc.phyloviewer.client.events.DataPayloadEventHandler;
import org.iplantc.phyloviewer.client.events.Messages;
import org.iplantc.phyloviewer.client.math.ConvertMathTypes;
import org.iplantc.phyloviewer.client.math.JsBox2;
import org.iplantc.phyloviewer.client.math.JsVector2;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Camera;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class TreeWidget extends Composite {
	public static final RenderPreferences renderPreferences = new RenderPreferences();
	
	public enum ViewType { VIEW_TYPE_CLADOGRAM, VIEW_TYPE_RADIAL }
	
	private class RenderTimer extends Timer {
		
		public void run() {
			if (TreeWidget.this.view.isReady()) {
				render();
			} else {
				this.schedule(33);
			}
		}
	}
	
	private HorizontalPanel mainPanel = new HorizontalPanel();
	private View view;
	private Timer renderTimer = new RenderTimer();
	private AnimateCamera animator;
	private SearchServiceAsyncImpl searchService;
	EventBus eventBus;
	
	public TreeWidget(SearchServiceAsyncImpl searchService,EventBus eventBus) {
		this.eventBus = eventBus;
		
		this.searchService = searchService;
		
		this.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
		
		this.initWidget(mainPanel);
		
		if(eventBus != null) {
			eventBus.addHandler(DataPayloadEvent.TYPE, new DataPayloadEventHandler() {

				@Override
				public void onFire(DataPayloadEvent event) {
					
					String message = event.getMessageString(); 
					if(message.equals(Messages.MESSAGE_NODE_CLICKED)) {
						JSONObject payload = event.getPayload();
						JsHit hit = (JsHit) JsonUtils.safeEval(payload.toString());
						
						JsVector2 p = hit.position();
						Vector2 position = ConvertMathTypes.convertToVector2(p);

						JsBox2 jsBox = hit.boundingBox();
						Box2D bbox = ConvertMathTypes.convertToBox2(jsBox);
						
						Camera finalCamera = view.getCamera().create();
						finalCamera.zoomToBoundingBox(position, bbox);
						
						startAnimation(finalCamera);
					}
				}
				
			});
		}
	}
	
	public void setDocument(IDocument document) {
		renderPreferences.clearHighlights();
		view.setDocument(document);
		view.zoomToFit();
		this.requestRender();
	}

	public void requestRender() {
		renderTimer.schedule(1);
	}
	
	public void resize(int width, int height) {
		
		if(null != view) {
			view.resize(width, height);
			this.requestRender();
		}
	}
	
	public void setViewType(ViewType type)
	{
		int width = 1000;
		int height = 800;
		
		IDocument document = null;
		
		if (null != view ) {
			width = view.getWidth();
			height = view.getHeight();
			
			document = view.getDocument();
			
			mainPanel.remove(this.view);
		}
		
		this.view = null;
		
		switch(type) {
		case VIEW_TYPE_CLADOGRAM:
			this.view = new ViewCladogram(width,height,searchService,eventBus);
			break;
		case VIEW_TYPE_RADIAL:
			this.view = new ViewCircular(width,height,searchService,eventBus);
			break;
		default:
			throw new IllegalArgumentException ( "Invalid view type." );
		}
		
		if(null != view ) {
			view.setRenderPreferences(renderPreferences);
			view.setDocument(document);
			
			mainPanel.add(view);
			
			view.zoomToFit();
		}
		
		this.requestRender();
	}
	
	protected void startAnimation(Camera finalCamera) {
		
		if(null!=view) {
			animator = new AnimateCamera(view.getCamera().getViewMatrix(),finalCamera.getViewMatrix(),25);
			
			renderTimer.scheduleRepeating(30);
		}
	}
	
	public void animateZoomToNode(INode node)
	{
		Camera finalCamera = view.getCamera().create();
		finalCamera.zoomToFitSubtree(node, view.getLayout());
		
		startAnimation(finalCamera);
	}
	
	private void render() {
		if(animator!=null && view != null) {
			view.getCamera().setViewMatrix(animator.getNextMatrix());
			
			if(animator.isDone()) {
				animator = null;
				
				renderTimer.cancel();
			}
		}
		
		view.render();
	}
	
	public String exportImageURL()
	{
		return this.view.exportImageURL();
	}
	
	public View getView() {
		return view;
	}
}
