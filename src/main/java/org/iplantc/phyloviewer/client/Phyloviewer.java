/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client;

import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget;

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

	private final class LoadExampleCommand implements Command {
		private int example;
		
		public LoadExampleCommand(int example) {
			this.example=example;
		}
		@Override
		public void execute() {
			fetchTree.fetchTree(this.example, new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable arg0) {
					Window.alert(arg0.getMessage());
				}
	
				@Override
				public void onSuccess(String arg0) {
					widget.loadFromJSON(arg0);
		            widget.requestRender();
				}
			
			});
		}
	}

	TreeWidget widget = new TreeWidget();
	FetchTreeAsync fetchTree = GWT.create(FetchTree.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		// Make the menu.
		MenuBar exampleMenu = new MenuBar(true);
		exampleMenu.addItem("Small", new LoadExampleCommand(Constants.SMALL_TREE));
		exampleMenu.addItem("50K", new LoadExampleCommand(Constants.FIFTY_K_TAXONS));
		exampleMenu.addItem("100K", new LoadExampleCommand(Constants.ONE_HUNDRED_K_TAXONS));
		exampleMenu.addItem("NCBI taxonomy", new LoadExampleCommand(Constants.NCBI_TAXONOMY));
		
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("Load Exampe", exampleMenu);
		fileMenu.addItem("Open from newick string", new Command() {

			@Override
			public void execute() {
				Phyloviewer.this.loadNewickString();
			}
			
		});
		
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
		
		// Draw for the first time.
		widget.requestRender();
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
