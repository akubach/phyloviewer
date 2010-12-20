/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer;

import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.TreeListService;
import org.iplantc.phyloviewer.client.services.TreeListServiceAsync;
import org.iplantc.phyloviewer.client.services.SearchServiceAsyncImpl.RemoteNodeSuggestion;
import org.iplantc.phyloviewer.client.tree.viewer.BranchStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.ContextMenu;
import org.iplantc.phyloviewer.client.tree.viewer.GlyphStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.LabelStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.NodeStyleWidget;
import org.iplantc.phyloviewer.client.tree.viewer.NodeTable;
import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget;
import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget.ViewType;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.StyleByLabel;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.Tree;

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

	/*private final class LadderizeCommand implements Command {
		private Direction dir = Direction.UP;
		
		@Override
		public void execute() {
			toggle();
			widget.ladderize(dir);
		}
		
		private void toggle() {
			dir = (dir == Direction.UP) ? Direction.DOWN : Direction.UP;
		}
	};*/

	TreeWidget widget;

	JSTreeList trees;
	
	CombinedServiceAsync combinedService = new CombinedServiceAsyncImpl();
	SearchServiceAsyncImpl searchService = new SearchServiceAsyncImpl();
	TreeListServiceAsync treeList = GWT.create(TreeListService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RemoteNode.setService(combinedService);
		RemoteLayout.setService(combinedService);
		
		EventBus eventBus = new SimpleEventBus();
		
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
		layoutMenu.addSeparator();
		
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
		
		//FIXME disabling ladderize menu item, since the RemoteLayoutService does not yet account for the client having changed the tree structure
		//Command nullCommand = new Command() { public void execute(){} };
		//layoutMenu.addItem("Ladderize (disabled)", nullCommand);

	    // Make a new menu bar.
	    MenuBar menu = new MenuBar();
	    menu.addItem("File", fileMenu);
	    menu.addItem("Layout", layoutMenu); //FIXME: circular layout is broken (commenting layout menu out)
	    menu.addItem("Style", styleMenu);
	    
	    // Make a search box
	    final SuggestBox searchBox = new SuggestBox(searchService);
	    searchBox.setLimit(10); //TODO make scrollable?
	    searchBox.addSelectionHandler(new SelectionHandler<Suggestion>()
		{
			@Override
			public void onSelection(SelectionEvent<Suggestion> event)
			{
				RemoteNode node = ((RemoteNodeSuggestion)event.getSelectedItem()).getNode();
				widget.show(node);
			}
		});
	    
	    ContextMenu contextMenuPanel = new ContextMenu(widget);
	    
	    //children of contextMenuPanel will automatically be signed up to get DocumentChangeEvents and SelectionEvents from the TreeWidget
	    contextMenuPanel.add(new NodeTable(), "Node details", 3);
	    contextMenuPanel.add(new NodeStyleWidget(widget.getView().getDocument()), "Node", 3);
		contextMenuPanel.add(new BranchStyleWidget(widget.getView().getDocument()), "Branch", 3);
		contextMenuPanel.add(new GlyphStyleWidget(widget.getView().getDocument()), "Glyph", 3);
		contextMenuPanel.add(new LabelStyleWidget(widget.getView().getDocument()), "Label", 3);
	    
		// Make the UI.
	    DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.EM);
	    mainPanel.addNorth(menu, 2);
	    mainPanel.addSouth(searchBox, 2);
	    mainPanel.addWest(contextMenuPanel, 20);
	    mainPanel.add(widget);
		HorizontalPanel searchPanel = new HorizontalPanel();
		searchPanel.add(new Label("Search:"));
		searchPanel.add(searchBox);
		mainPanel.add(searchPanel);
	    RootLayoutPanel.get().add(mainPanel);
		
		// Draw for the first time.
		RootLayoutPanel.get().forceLayout();
		mainPanel.forceLayout();
		widget.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
		widget.render();
	}

	private void displayTrees() {
		final PopupPanel displayTreePanel = new PopupPanel();
		
		displayTreePanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = (Window.getClientWidth() - offsetWidth) / 3;
	            int top = (Window.getClientHeight() - offsetHeight) / 3;
	            displayTreePanel.setPopupPosition(left, top);
	          }
	    });
		
		final VerticalPanel vPanel = new VerticalPanel();
		displayTreePanel.add(vPanel);
		
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
					JSTreeData data = trees.getTree ( index );
					if ( data != null ) {
						combinedService.getTree(data.getId(), new AsyncCallback<Tree>() {

							@Override
							public void onFailure(Throwable arg0) {
								Window.alert(arg0.getMessage());
								
								displayTreePanel.hide();
							}
				
							@Override
							public void onSuccess(Tree tree) {
								searchService.setTree(tree);
								Document document = new Document();
								document.setTree(tree);
								document.setLayout(new RemoteLayout());
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
