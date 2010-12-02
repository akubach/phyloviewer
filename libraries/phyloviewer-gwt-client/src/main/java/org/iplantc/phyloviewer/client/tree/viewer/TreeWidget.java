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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class TreeWidget extends Composite {
	public static final RenderPreferences renderPreferences = new RenderPreferences();
	
	public enum ViewType { VIEW_TYPE_CLADOGRAM, VIEW_TYPE_RADIAL }
	
	private HorizontalPanel mainPanel = new HorizontalPanel();
	private View view;
	private SearchServiceAsyncImpl searchService;
	EventBus eventBus;
	
	public TreeWidget(SearchServiceAsyncImpl searchService,EventBus eventBus) {
		this.eventBus = eventBus;
		
		this.searchService = searchService;
		
		this.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
		
		this.initWidget(mainPanel);
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
