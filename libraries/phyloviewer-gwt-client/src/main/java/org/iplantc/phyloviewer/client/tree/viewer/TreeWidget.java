/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.tree.viewer.event.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.tree.viewer.event.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.tree.viewer.event.HasDocument;
import org.iplantc.phyloviewer.client.tree.viewer.event.HasNodeSelectionHandlers;
import org.iplantc.phyloviewer.client.tree.viewer.event.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.tree.viewer.event.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class TreeWidget extends ResizeComposite implements HasDocument, HasNodeSelectionHandlers {
	public static final RenderPreferences renderPreferences = new RenderPreferences();
	
	public enum ViewType { VIEW_TYPE_CLADOGRAM, VIEW_TYPE_RADIAL }
	
	private LayoutPanel mainPanel = new LayoutPanel();
	private View view;
	private SearchServiceAsyncImpl searchService;
	EventBus eventBus;
	IDocument document;
	
	public TreeWidget(SearchServiceAsyncImpl searchService, EventBus eventBus) {
		this.searchService = searchService;
		this.eventBus = eventBus;
	
		this.initWidget(mainPanel);
		this.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
	}
	
	@Override
	public void setDocument(IDocument document) {
		this.document = document;
		renderPreferences.clearHighlights();
		view.setDocument(document);
		view.zoomToFit();
		view.requestRender();
		
		eventBus.fireEventFromSource(new DocumentChangeEvent(document), this);
	}
	
	@Override
	public HandlerRegistration addDocumentChangeHandler(DocumentChangeHandler handler)
	{
		return eventBus.addHandlerToSource(DocumentChangeEvent.TYPE, this, handler);
	}

	@Override
	public IDocument getDocument()
	{
		return document;
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(NodeSelectionHandler handler)
	{
		return eventBus.addHandlerToSource(NodeSelectionEvent.TYPE, this, handler);
	}
	
//	public void resize(int width, int height) {
//		
//		if(null != view) {
//			view.resize(width, height);
//			view.requestRender();
//		}
//	}
	
	public void setViewType(ViewType type)
	{
		int width = Math.max(10, getOffsetWidth());
		int height = Math.max(10, getOffsetHeight());
			
		View newView = createView(type, width, height);
		
		if(null != newView ) {
			setView(newView);
		}
	}

	private View createView(ViewType type, int width, int height)
	{
		switch (type)
		{
			case VIEW_TYPE_CLADOGRAM:
				return new ViewCladogram(width, height, this.searchService, this.eventBus);
			case VIEW_TYPE_RADIAL:
				return new ViewCircular(width, height, this.searchService, this.eventBus);
			default:
				throw new IllegalArgumentException("Invalid view type.");
		}
	}

	private void removeCurrentView()
	{
		if (null != view ) {
			mainPanel.remove(this.view);
			this.view = null;
		}
	}

	private void setView(View newView)
	{
		removeCurrentView();
		
		this.view = newView;
		newView.setRenderPreferences(renderPreferences);
		newView.setDocument(document);
		
		//if the document is somehow changed directly in the view, TreeWidget should update and refire the event
		newView.addDocumentChangeHandler(new DocumentChangeHandler()
		{
			@Override
			public void onDocumentChange(DocumentChangeEvent event)
			{
				TreeWidget.this.document = event.getDocument();
				eventBus.fireEventFromSource(new DocumentChangeEvent(document), this);
			}
		});
		
		//refires selection events from the view, with the TreeWidget as the source
		newView.addSelectionHandler(new NodeSelectionHandler()
		{
			@Override
			public void onNodeSelection(NodeSelectionEvent event)
			{
				eventBus.fireEventFromSource(new NodeSelectionEvent(event.getSelectedNodes()), TreeWidget.this);
			}
		});
		
		mainPanel.add(newView);
		
		newView.zoomToFit();
		
		newView.requestRender();
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
