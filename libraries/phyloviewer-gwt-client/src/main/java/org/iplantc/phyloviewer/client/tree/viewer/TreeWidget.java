/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.events.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.events.HasDocument;
import org.iplantc.phyloviewer.client.events.HasNodeSelectionHandlers;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.render.HasRenderPreferences;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class TreeWidget extends ResizeComposite implements HasDocument, HasNodeSelectionHandlers, HasRenderPreferences
{
	public RenderPreferences renderPreferences = new RenderPreferences();

	public enum ViewType
	{
		VIEW_TYPE_CLADOGRAM, VIEW_TYPE_RADIAL
	}

	private LayoutPanel mainPanel = new LayoutPanel();
	private View view;
	private SearchServiceAsyncImpl searchService;
	EventBus eventBus;
	IDocument document;

	public TreeWidget(SearchServiceAsyncImpl searchService, EventBus eventBus)
	{
		this.searchService = searchService;
		this.eventBus = eventBus;

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
				newView = new ViewCladogram(width, height, this.searchService);
				detail = ((ViewCladogram)newView).getDetailView();
				break;
			case VIEW_TYPE_RADIAL:
				newView = detail = new ViewCircular(width, height, this.searchService);
				break;
			default:
				throw new IllegalArgumentException("Invalid view type.");
		}
		
		newView.setEventBus(this.eventBus);
		detail.setDefaults();
		
		return newView;
	}

	private void removeCurrentView()
	{
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

		view.addKeyPressHandler(new KeyPressHandler()
		{

			@Override
			public void onKeyPress(KeyPressEvent arg0)
			{
				final char charCode = arg0.getCharCode();
				if(charCode == ' ')
				{
					view.zoomToFit();
				}
			}

		});

		view.addKeyUpHandler(new KeyUpHandler()
		{

			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				if(event.isUpArrow())
				{
					view.pan(0.0, 0.1);
				}
				else if(event.isDownArrow())
				{
					view.pan(0.0, -0.1);
				}
				else if(event.isLeftArrow())
				{
					view.pan(0.1, 0.0);
				}
				else if(event.isRightArrow())
				{
					view.pan(-0.1, 0.0);
				}
			}
		});

		// if the document is somehow changed directly in the view, TreeWidget should update and refire
		// the event
		newView.addDocumentChangeHandler(new DocumentChangeHandler()
		{
			@Override
			public void onDocumentChange(DocumentChangeEvent event)
			{
				TreeWidget.this.document = event.getDocument();
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
		getView().setRenderPreferences(rp);
	}
}
