/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public class TreeWidget extends LayoutPanel implements RequiresResize, ProvidesResize {
	public static final RenderPreferences renderPreferences = new RenderPreferences();
	
	public enum ViewType { VIEW_TYPE_CLADOGRAM, VIEW_TYPE_RADIAL }
	
//	private FlowPanel mainPanel = new FlowPanel();
	private View view;
	private SearchServiceAsyncImpl searchService;
	EventBus eventBus;
	
	public TreeWidget(SearchServiceAsyncImpl searchService, EventBus eventBus) {
		this.searchService = searchService;
		this.eventBus = eventBus;
	
//		this.initWidget(mainPanel);
		this.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
	}
	
	public void setDocument(IDocument document) {
		renderPreferences.clearHighlights();
		view.setDocument(document);
		view.zoomToFit();
		view.requestRender();
	}
	
	public void resize(int width, int height) {
		
		if(null != view) {
			view.resize(width, height);
			view.requestRender();
		}
	}
	
	public void setViewType(ViewType type)
	{
		int width = 100;
		int height = 100;
		
		if (getOffsetWidth() > 0) 
		{
			width = getOffsetWidth();
		}
		
		if (getOffsetHeight() > 0)
		{
			height = getOffsetHeight();
		}
		
		IDocument document = null;
		
		if (null != view ) {
			width = view.getWidth();
			height = view.getHeight();
			
			document = view.getDocument();
			
			this.remove(this.view);
		}
		
		this.view = null;
		
		switch(type) {
		case VIEW_TYPE_CLADOGRAM:
			this.view = new ViewCladogram(width, height, this.searchService, this.eventBus);
			break;
		case VIEW_TYPE_RADIAL:
			this.view = new ViewCircular(width, height, this.searchService, this.eventBus);
			break;
		default:
			throw new IllegalArgumentException ( "Invalid view type." );
		}
		
		if(null != view ) {
			view.setRenderPreferences(renderPreferences);
			view.setDocument(document);
			
			this.add(view);
			
			view.zoomToFit();
		}
		
		view.requestRender();
	}
	
	public void render() {
		view.render();
	}
	
	public String exportImageURL()
	{
		return this.view.exportImageURL();
	}
	
	public View getView() {
		return view;
	}
	
	/**
	 * Shows a node in the current tree.  If the node and its ancestors aren't currently in the tree, they are loaded first.
	 * @param node
	 */
	public void show(INode node)
	{
		if (node instanceof RemoteNode && view.getTree().getRootNode() instanceof RemoteNode)
		{
			// node could be in a part of the tree that hasn't been loaded yet.  Zooming to this node will be a problem.  Make sure all of its ancestors are loaded before trying to show it.
			RemoteNode root = (RemoteNode)view.getTree().getRootNode();
			root.ensureNodeInSubtree((RemoteNode)node, new AsyncCallback<RemoteNode>() 
			{
				@Override
				public void onSuccess(RemoteNode node) {
					view.zoomToFitSubtree(node);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Logger.getLogger("").log(Level.FINE, "Failed to attach node to tree", caught);
				}
			});
		}
		else 	
		{
			view.zoomToFitSubtree(node);
		}
	}
}
