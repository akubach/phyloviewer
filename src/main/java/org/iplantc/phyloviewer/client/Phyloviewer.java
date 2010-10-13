/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client;

import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;

import org.iplantc.phyloviewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.client.services.CombinedServiceAsyncImpl;
import org.iplantc.phyloviewer.client.services.TreeListService;
import org.iplantc.phyloviewer.client.services.TreeListServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

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

	TreeWidget widget = new TreeWidget();

	JSTreeList trees;
	
	CombinedServiceAsync combinedService = new CombinedServiceAsyncImpl();
	TreeListServiceAsync treeList = GWT.create(TreeListService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("Open...", new Command() {

			@Override
			public void execute() {
				Phyloviewer.this.displayTrees();
			}
			
		});
		
		//FIXME handle tree uploads in RemoteNodeService and the client and then re-enable this menu item
//		fileMenu.addItem("Open from newick string", new Command() {
//
//			@Override
//			public void execute() {
//				Phyloviewer.this.loadNewickString();
//			}
//			
//		});
		
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
		
		//FIXME disabling ladderize menu item, since the RemoteLayoutService does not yet account for the client having changed the tree structure
		//Command nullCommand = new Command() { public void execute(){} };
		//layoutMenu.addItem("Ladderize (disabled)", nullCommand);

	    // Make a new menu bar.
	    MenuBar menu = new MenuBar();
	    menu.addItem("File", fileMenu);
	    menu.addItem("Layout", layoutMenu);
		
		// Make the UI.
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(menu);
		
		mainPanel.add(widget);
	  
		RootPanel.get().add(mainPanel);
			
		widget.resize(Window.getClientWidth(),Window.getClientHeight()-50);
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				widget.resize(event.getWidth(),Window.getClientHeight()-50);
			}
			
		});
		
		RemoteNode.setService(combinedService);
		RemoteLayout.setService(combinedService);
		
		// Draw for the first time.
		widget.requestRender();
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
								widget.setTree(tree);
								
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

	private void loadNewickString() {
		
		final PopupPanel loadTreePanel = new PopupPanel();
		
		loadTreePanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = (Window.getClientWidth() - offsetWidth) / 3;
	            int top = (Window.getClientHeight() - offsetHeight) / 3;
	            loadTreePanel.setPopupPosition(left, top);
	          }
	    });
		
		final FormPanel treeForm = new FormPanel();
		treeForm.setAction("/parseTree");
		//treeForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		treeForm.setEncoding(FormPanel.ENCODING_URLENCODED);
		treeForm.setMethod(FormPanel.METHOD_POST);
		
		loadTreePanel.add(treeForm);

		// Create a panel to hold all of the form widgets.
	    VerticalPanel panel = new VerticalPanel();
	    treeForm.setWidget(panel);
	    
	    Label label = new Label ("Enter newick string:");
	    panel.add(label);
		final TextArea tb = new TextArea();
	    tb.setName("textBoxFormElement");
	    panel.add(tb);
	    
	    //FileUpload upload = new FileUpload();
	    //upload.setName("uploadFormElement");
	    //panel.add(upload);
	    
	    HorizontalPanel hPanel = new HorizontalPanel();
	    
		// Add a 'submit' button.
	    hPanel.add(new Button("Submit", new ClickHandler() {
	      public void onClick(ClickEvent event) {
	    	  treeForm.submit();
	      }
	    }));
	    
	    hPanel.add(new Button("Cancel", new ClickHandler() {
	      public void onClick(ClickEvent event) {
	    	  loadTreePanel.hide();
	      }
	    }));
	    
	    panel.add(hPanel);
	    
		// Add an event handler to the form.
	    treeForm.addSubmitHandler(new FormPanel.SubmitHandler() {
	      public void onSubmit(SubmitEvent event) {
	        // This event is fired just before the form is submitted. We can take
	        // this opportunity to perform validation.
	        if (tb.getText().length() == 0) {
	          Window.alert("The text box must not be empty");
	          event.cancel();
	        }
	      }
	    });
	    treeForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
	      public void onSubmitComplete(SubmitCompleteEvent event) {
	        widget.loadFromJSON(event.getResults());
            widget.requestRender();
            loadTreePanel.hide();
	      }
	    });
	    
	    loadTreePanel.show();
	}
}
