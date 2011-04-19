/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.client;

import org.iplantc.phyloviewer.client.events.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.events.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.events.HasDocument;
import org.iplantc.phyloviewer.client.events.HasNodeSelectionHandlers;
import org.iplantc.phyloviewer.client.events.HighlightSelectionHandler;
import org.iplantc.phyloviewer.client.events.NavigationMode;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.events.SelectionMode;
import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.client.tree.viewer.View;
import org.iplantc.phyloviewer.client.tree.viewer.ViewCircular;
import org.iplantc.phyloviewer.client.tree.viewer.ViewCladogram;
import org.iplantc.phyloviewer.client.tree.viewer.render.HasRenderPreferences;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;
import org.iplantc.phyloviewer.viewer.client.render.SearchHighlighter;
import org.iplantc.phyloviewer.viewer.client.services.SearchServiceAsyncImpl;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class TreeWidget extends ResizeComposite implements HasDocument, HasNodeSelectionHandlers,
		HasRenderPreferences
{
	public RenderPreferences renderPreferences = new RenderPreferences();

	public enum ViewType
	{
		VIEW_TYPE_CLADOGRAM, VIEW_TYPE_RADIAL
	}

	private LayoutPanel mainPanel = new LayoutPanel();
	private View view;
	private EventBus eventBus;
	private IDocument document;
	private SearchHighlighter highlighter;

	public TreeWidget(SearchServiceAsyncImpl searchService, EventBus eventBus)
	{
		this.eventBus = eventBus;
		this.highlighter = new SearchHighlighter(searchService);
		highlighter.setRenderPreferences(renderPreferences);

		this.initWidget(mainPanel);
		this.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
	}

	@Override
	public void setDocument(IDocument document)
	{
		this.document = document;
		renderPreferences.clearAllHighlights();
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

	public void setViewType(ViewType type)
	{
		int width = Math.max(10, getOffsetWidth());
		int height = Math.max(10, getOffsetHeight());

		View newView = createView(type, width, height);

		if(null != newView)
		{
			setView(newView);
		}
	}

	private View createView(ViewType type, int width, int height)
	{
		View newView = null;
		DetailView detail = null;

		switch (type)
		{
			case VIEW_TYPE_CLADOGRAM:
				newView = new ViewCladogram(width, height);
				detail = ((ViewCladogram)newView).getDetailView();
				break;
			case VIEW_TYPE_RADIAL:
				newView = detail = new ViewCircular(width, height);
				break;
			default:
				throw new IllegalArgumentException("Invalid view type.");
		}

		newView.setEventBus(this.eventBus);
		
		final SelectionMode selectionMode = new SelectionMode(detail);
		selectionMode.getMouseHandler().addSelectionHandler(new HighlightSelectionHandler(detail));
		
		//refire selection events with this TreeWidget as the source.
		selectionMode.getMouseHandler().addSelectionHandler(new NodeSelectionHandler()
		{
			@Override
			public void onNodeSelection(NodeSelectionEvent event)
			{
				eventBus.fireEventFromSource(new NodeSelectionEvent(event.getSelectedNodes()),
						TreeWidget.this);
			}
		});
		
		final NavigationMode navMode = new NavigationMode(detail);
		
		final DetailView dView = detail; //need a final ref to use in the handler
		detail.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == 's')
				{
					dView.setInteractionMode(selectionMode);
				}
				else if(event.getCharCode() == 'n')
				{
					dView.setInteractionMode(navMode);
				}
			}
		});
		
		detail.addMouseOverHandler(new MouseOverHandler()
		{
			@Override
			public void onMouseOver(MouseOverEvent event)
			{
				dView.setFocus(true);
			}
		});
		
		 //TODO add these two methods to View and get rid of the DetailView references in the rest of createView
		detail.setInteractionMode(navMode);
		detail.setDrawRenderStats(false);

		return newView;
	}

	private void removeCurrentView()
	{
		if(highlighter != null)
		{
			highlighter.clear();
		}

		if(null != view)
		{
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
		
		if(highlighter != null)
		{
			highlighter.setView(newView);
		}

		// if the document is somehow changed directly in the view, TreeWidget should update and refire
		// the event
		newView.addDocumentChangeHandler(new DocumentChangeHandler()
		{
			@Override
			public void onDocumentChange(DocumentChangeEvent event)
			{
				TreeWidget.this.document = event.getDocument();

				if(highlighter != null)
				{
					if(document != null)
					{
						highlighter.setTree(document.getTree());
					}
					else
					{
						highlighter.setTree(null);
					}
				}

				eventBus.fireEventFromSource(new DocumentChangeEvent(document), this);
			}
		});

		// refires selection events from the view, with the TreeWidget as the source
		newView.addSelectionHandler(new NodeSelectionHandler()
		{
			@Override
			public void onNodeSelection(NodeSelectionEvent event)
			{
				eventBus.fireEventFromSource(new NodeSelectionEvent(event.getSelectedNodes()),
						TreeWidget.this);
			}
		});

		mainPanel.add(newView);

		newView.zoomToFit();

		newView.requestRender();
	}

	public void render()
	{
		view.render();
	}

	public String exportImageURL()
	{
		return this.view.exportImageURL();
	}

	public View getView()
	{
		return view;
	}

	/**
	 * Make the bounding box fill the viewport.
	 * 
	 * @param box
	 */

	public void show(Box2D box)
	{
		view.zoomToBoundingBox(box);
	}

	@Override
	public RenderPreferences getRenderPreferences()
	{
		return this.renderPreferences;
	}

	@Override
	public void setRenderPreferences(RenderPreferences rp)
	{
		this.renderPreferences = rp;
		highlighter.setRenderPreferences(rp);
		getView().setRenderPreferences(rp);
	}
}
