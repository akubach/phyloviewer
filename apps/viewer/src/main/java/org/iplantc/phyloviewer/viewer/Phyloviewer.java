/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer;

import org.iplantc.phyloviewer.client.services.CombinedService.NodeResponse;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.TreeListService;
import org.iplantc.phyloviewer.client.services.TreeListServiceAsync;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl.RemoteNodeSuggestion;
import org.iplantc.phyloviewer.client.tree.viewer.BranchStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.ColorBox;
import org.iplantc.phyloviewer.client.tree.viewer.ContextMenu;
import org.iplantc.phyloviewer.client.tree.viewer.GlyphStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.LabelStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.NodeStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.NodeTable;
import org.iplantc.phyloviewer.client.tree.viewer.PagedDocument;
import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget;
import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget.ViewType;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.StyleByLabel;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.model.Document;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Phyloviewer implements EntryPoint {

	TreeWidget widget;

	JSTreeList trees;
	
	CombinedServiceAsync combinedService = new CombinedServiceAsyncImpl();
	SearchServiceAsyncImpl searchService = new SearchServiceAsyncImpl();
	TreeListServiceAsync treeList = GWT.create(TreeListService.class);
	
	EventBus eventBus = new SimpleEventBus();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		widget = new TreeWidget(searchService,eventBus);
		
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("Open...", new Command() {

			@Override
			public void execute() {
				Phyloviewer.this.displayTrees();
			}
			
		});
		
		Command openURL = new Command() {
			@Override
			public void execute()
			{
				Window.open(widget.exportImageURL(), "_blank", "");
			}
		};
		
		fileMenu.addItem("Get image (opens in a popup window)", openURL);
	
		MenuBar layoutMenu = new MenuBar(true);
		layoutMenu.addItem("Rectangular", new Command() {
			@Override
			public void execute() {
				widget.setViewType(TreeWidget.ViewType.VIEW_TYPE_CLADOGRAM);
			}
		});
		layoutMenu.addItem("Circular", new Command() {
			@Override
			public void execute() {
				widget.setViewType(TreeWidget.ViewType.VIEW_TYPE_RADIAL);
			}
		});
		
		MenuBar styleMenu = new MenuBar(true);
		final TextInputPopup styleTextPopup = new TextInputPopup();
		styleTextPopup.addValueChangeHandler(new ValueChangeHandler<String>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				StyleByLabel styleMap = new StyleByLabel();
				styleMap.put(event.getValue());
				widget.getView().getDocument().setStyleMap(styleMap);
				widget.render();
			}
		});

		styleTextPopup.setModal(true);
		
		styleMenu.addItem("Style by CSV", new Command()
		{
			@Override
			public void execute()
			{
				styleTextPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback()
				{	
					@Override
					public void setPosition(int offsetWidth, int offsetHeight)
					{
			            int left = (Window.getClientWidth() - offsetWidth) / 3;
			            int top = (Window.getClientHeight() - offsetHeight) / 3;
			            styleTextPopup.setPopupPosition(left, top);
					}
				});
			}
		});
	    
	    // Make a search box
	    final SuggestBox searchBox = new SuggestBox(searchService);
	    searchBox.setLimit(10); //TODO make scrollable?
	    searchBox.addSelectionHandler(new SelectionHandler<Suggestion>()
		{
			@Override
			public void onSelection(SelectionEvent<Suggestion> event)
			{
				Box2D box = ((RemoteNodeSuggestion)event.getSelectedItem()).getResult().layout.boundingBox;
				widget.show(box);
			}
		});
	    
	    //create some styling widgets for the context menu
	    NodeStyleWidget nodeStyleWidget = new NodeStyleWidget(widget.getView().getDocument());
	    BranchStyleWidget branchStyleWidget = new BranchStyleWidget(widget.getView().getDocument());
	    GlyphStyleWidget glyphStyleWidget = new GlyphStyleWidget(widget.getView().getDocument());
	    LabelStyleWidget labelStyleWidget = new LabelStyleWidget(widget.getView().getDocument());
	    
	    //replace their default TextBoxes with ColorBoxes, which jscolor.js will add a color picker to
	    nodeStyleWidget.setColorWidget(new ColorBox());
	    branchStyleWidget.setStrokeColorWidget(new ColorBox());
	    glyphStyleWidget.setStrokeColorWidget(new ColorBox());
	    glyphStyleWidget.setFillColorWidget(new ColorBox());
	    labelStyleWidget.setColorWidget(new ColorBox());
	    
	    //add the widgets to separate panels on the context menu
	    final ContextMenu contextMenuPanel = new ContextMenu(widget);
	    contextMenuPanel.add(new NodeTable(), "Node details", 3);
		contextMenuPanel.add(nodeStyleWidget, "Node", 3);
		contextMenuPanel.add(branchStyleWidget, "Branch", 3);
		contextMenuPanel.add(glyphStyleWidget, "Glyph", 3);
		contextMenuPanel.add(labelStyleWidget, "Label", 3);
	    
		HorizontalPanel searchPanel = new HorizontalPanel();
		searchPanel.add(new Label("Search:"));
		searchPanel.add(searchBox);
		
		// Make the UI.
		MenuBar menu = new MenuBar();
	    final DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.EM);
	    mainPanel.addNorth(menu, 2);
	    mainPanel.addSouth(searchPanel, 2);
	    mainPanel.addWest(contextMenuPanel, 0);
	    mainPanel.add(widget);
	    RootLayoutPanel.get().add(mainPanel);
	    
	    MenuBar viewMenu = new MenuBar(true);
	    viewMenu.addItem("Layout", layoutMenu);
	    viewMenu.addItem("Style", styleMenu);
	    
	    contextMenuPanel.setVisible(false);
	    viewMenu.addItem("Toggle Context Panel", new Command()
		{
			@Override
			public void execute()
			{
				if(contextMenuPanel.isVisible()) {
					contextMenuPanel.setVisible(false);
				    mainPanel.setWidgetSize(contextMenuPanel,0);
				    mainPanel.forceLayout();
				}
				else {
					contextMenuPanel.setVisible(true);
					mainPanel.setWidgetSize(contextMenuPanel, 20);
					mainPanel.forceLayout();
				}
			}
		});
	    
	    menu.addItem("File", fileMenu);
	    menu.addItem("View", viewMenu);
		
		// Draw for the first time.
		RootLayoutPanel.get().forceLayout();
		mainPanel.forceLayout();
		widget.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
		widget.render();
		
		initColorPicker();
		
		// Present the user the dialog to load a tree.
		this.displayTrees();
	}

	private final native void initColorPicker()
	/*-{
		$wnd.jscolor.init();
	}-*/;

	private void displayTrees() {
		final PopupPanel displayTreePanel = new PopupPanel();
		displayTreePanel.setModal(true);
		
		displayTreePanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	        	  int left = (Window.getClientWidth() - offsetWidth) / 2;
	        	  int top = (Window.getClientHeight() - offsetHeight)/2 - 100;
	        	  displayTreePanel.setPopupPosition(left, top);
	          }
	    });
		
		final VerticalPanel vPanel = new VerticalPanel();
		displayTreePanel.add(vPanel);
		
		Label messageLabel = new Label ("Select a tree to load:");
		vPanel.add(messageLabel);
		
		final Label label = new Label ( "Retrieving tree list...");
		vPanel.add(label);
		
		final ListBox lb = new ListBox();
		lb.setVisible(false);
		vPanel.add(lb);
		
		final HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(new Button("OK",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {

				label.setText("Loading...");
				label.setVisible(true);
				lb.setVisible(false);
				hPanel.setVisible(false);
				
				int index = lb.getSelectedIndex();
				if ( index >= 0 && trees != null ) {
					final JSTreeData data = trees.getTree ( index );
					if ( data != null ) {
						combinedService.getRootNode(data.getId(), new AsyncCallback<NodeResponse>() {

							@Override
							public void onFailure(Throwable arg0) {
								Window.alert(arg0.getMessage());
								
								displayTreePanel.hide();
							}
				
							@Override
							public void onSuccess(NodeResponse response) {
								Document document = new PagedDocument(combinedService, eventBus, data.getId(), response);
								searchService.setTree(document.getTree());
								widget.setDocument(document);
								displayTreePanel.hide();
							}

						});
					}
				}
			}
		}));
		
		hPanel.add(new Button("Cancel",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				displayTreePanel.hide();
			}
		}));
		
		vPanel.add(hPanel);
		
		displayTreePanel.show();
				
		treeList.getTreeList(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable arg0) {
				Window.alert(arg0.getMessage());
			}

			@Override
			public void onSuccess(String json) {
				trees = JSTreeList.parseJSON(json);
		
				for(int i = 0; i < trees.getNumberOfTrees(); ++i ) {
					lb.addItem(trees.getTree(i).getName());
				}
				
				label.setVisible(false);
				lb.setVisible(true);
			}
		});
	}
	
	private class TextInputPopup extends PopupPanel implements HasValueChangeHandlers<String>
	{	
		public TextInputPopup()
		{
			VerticalPanel vPanel = new VerticalPanel();
			final TextArea textBox = new TextArea();
			textBox.setVisibleLines(20);
			textBox.setCharacterWidth(80);
			Button okButton = new Button("OK", new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					ValueChangeEvent.fire(TextInputPopup.this, textBox.getValue());
					TextInputPopup.this.hide();
				}
			});
			
			vPanel.add(textBox);
			vPanel.add(okButton);
			this.add(vPanel);
		}
		
		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
		{
			return addHandler(handler, ValueChangeEvent.getType());
		}		
	}
}
