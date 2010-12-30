package org.iplantc.recon.client;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.client.tree.viewer.NodeClickedHandler;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ReconViz implements EntryPoint {
	
	class MyTreeWidget extends Composite {
		
		DetailView view;
		
		MyTreeWidget(EventBus eventBus) {
			
			view = new DetailView(800,600,null,eventBus);
			
			this.initWidget(view);
		}
		
		void setJSONData(String treeData, String layoutData ) {
			JsDocument doc = getDocument( "(" + treeData + ") ");
			JsLayoutCladogram layout = getLayout( "(" + layoutData + ")" );
			
			Document document = new Document();
			document.setTree(doc.getTree());
			document.setStyleMap(doc.getStyleMap());
			document.setLayout(layout);
			
			view.setDocument(document);
		}

		public DetailView getView() {
			return view;
		}
	}
	
	private final static native JsDocument getDocument(String json) /*-{ return eval(json); }-*/;
	private final static native JsLayoutCladogram getLayout(String json) /*-{ return eval(json); }-*/;
	
	MyTreeWidget leftTreeWidget;
	MyTreeWidget rightTreeWidget;
	int trees = 0;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  EventBus eventBus = new SimpleEventBus();
	  leftTreeWidget = new MyTreeWidget(eventBus);
	  rightTreeWidget = new MyTreeWidget(eventBus);
	  
	  leftTreeWidget.getView().addNodeClickedHandler(new NodeClickedHandler() {

		@Override
		public void onNodeClicked(INode node) {
			
			rightTreeWidget.getView().getRenderer().getRenderPreferences().clearHighlights();
			
			int numNodes = rightTreeWidget.getView().getTree().getNumberOfNodes();
			int numHighlights = 3;// Math.max(1, (int) Math.random() * 5);
			
			for(int i = 0; i < numHighlights; ++i) {
				int id = 1 + (int) Random.nextInt(numNodes - 2);
				rightTreeWidget.getView().getRenderer().getRenderPreferences().highlight(id);
			}
			
			rightTreeWidget.getView().requestRender();
		}
	  });
	  
	// Create a FormPanel and point it at a service.
    final FormPanel form = new FormPanel();
    form.setAction("/parseFile");

    // Because we're going to add a FileUpload widget, we'll need to set the
    // form to use the POST method, and multipart MIME encoding.
    form.setEncoding(FormPanel.ENCODING_MULTIPART);
    form.setMethod(FormPanel.METHOD_POST);

    // Create a panel to hold all of the form widgets.
    VerticalPanel panel = new VerticalPanel();
    form.setWidget(panel);

    FileUpload upload = new FileUpload();
    upload.setName("file");
    panel.add(upload);

    // Add a 'submit' button.
    panel.add(new Button("Submit", new ClickHandler() {
    	@Override
    	public void onClick(ClickEvent arg0) {
    		form.submit();
    	}
    }));
    
    form.addSubmitHandler(new FormPanel.SubmitHandler() {

		@Override
		public void onSubmit(SubmitEvent event) {
			// TODO: form validation?
		}
    });
    
    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
        public void onSubmitComplete(SubmitCompleteEvent event) {
          
        	final String jsonTree = event.getResults();
          
        	String url = "/layout";
        	RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
        	try {
        		Request request = builder.sendRequest(jsonTree, new RequestCallback() {
				
					@Override
					public void onResponseReceived(Request arg0, Response arg1) {
						
						if ( trees % 2 == 0 ) {
							leftTreeWidget.setJSONData(jsonTree, arg1.getText());
						}
						else {
							rightTreeWidget.setJSONData(jsonTree, arg1.getText());
						}
						++trees;
					}
					
					@Override
					public void onError(Request arg0, Throwable arg1) {
						// TODO Auto-generated method stub
						
					}
				});
			} catch (RequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	          
	     }
	  });

    VerticalPanel outerPanel = new VerticalPanel();
    outerPanel.add(form);
    
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add(leftTreeWidget);
    hPanel.add(rightTreeWidget);
    outerPanel.add(hPanel);
    
      RootPanel.get().add(outerPanel);
  }
}
