package org.iplantc.recon.client;

import org.iplantc.core.broadcaster.shared.BroadcastCommand;
import org.iplantc.phyloviewer.client.tree.viewer.DetailView;
import org.iplantc.phyloviewer.client.tree.viewer.model.JsDocument;
import org.iplantc.phyloviewer.shared.model.Document;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ReconViz implements EntryPoint
{
	class MyTreeWidget extends ContentPanel
	{
		DetailView view;

		MyTreeWidget()
		{
			setStyleAttribute("margin", "5px");
			setScrollMode(Scroll.AUTO);
			setHeading("Tree view");

			view = new DetailView(800, 600);
			view.setDefaults();
			this.add(view);
			this.setSize("800", "400");

			setTopComponent(buildToolbar());
		}

		private Button buildHomeButton()
		{
			Button button = new Button("Home", new SelectionListener<ButtonEvent>()
			{

				@Override
				public void componentSelected(ButtonEvent ce)
				{
					view.zoomToFit();
				}
			});

			return button;
		}

		private ToolBar buildToolbar()
		{
			ToolBar toolbar = new ToolBar();

			toolbar.add(new Button("Open", new SelectionListener<ButtonEvent>()
			{

				@Override
				public void componentSelected(ButtonEvent ce)
				{
					loadFile();
				}
			}));

			toolbar.add(buildHomeButton());

			return toolbar;
		}

		private void loadFile()
		{
			final Dialog dialog = new Dialog();
			dialog.setButtons(Dialog.OKCANCEL);

			final FormPanel panel = new FormPanel();
			panel.setHeading("Load file");
			panel.setFrame(true);
			panel.setAction("/parseFile");
			panel.setEncoding(Encoding.MULTIPART);
			panel.setMethod(Method.POST);
			panel.setButtonAlign(HorizontalAlignment.CENTER);
			panel.setWidth(350);

			FileUploadField file = new FileUploadField();
			file.setAllowBlank(false);
			file.setName("uploadedfile");
			file.setFieldLabel("File");
			panel.add(file);

			dialog.getButtonById("ok").addSelectionListener(new SelectionListener<ButtonEvent>()
			{
				@Override
				public void componentSelected(ButtonEvent ce)
				{
					if(!panel.isValid())
					{
						return;
					}

					panel.submit();

				}
			});

			dialog.getButtonById("cancel").addSelectionListener(new SelectionListener<ButtonEvent>()
			{
				@Override
				public void componentSelected(ButtonEvent ce)
				{
					dialog.hide();
				}
			});

			panel.addListener(Events.Submit, new Listener<FormEvent>()
			{
				public void handleEvent(FormEvent arg0)
				{
					String result = arg0.getResultHtml();
					setJSONData(result);

					dialog.hide();
				}
			});

			dialog.add(panel);

			dialog.show();
		}

		void setJSONData(String treeData)
		{
			JsDocument doc = getDocument("(" + treeData + ") ");

			Document document = new Document();
			document.setTree(doc.getTree());
			document.setStyleMap(doc.getStyleMap());
			document.setLayout(doc.getLayout());

			view.setDocument(document);
			view.requestRender();

			view.highlightNode(2);
			view.highlightSubtree(8);
			view.highlightBranch(7);

			view.lockToMaximumZoom();
		}

		public DetailView getView()
		{
			return view;
		}
	}

	private final static native JsDocument getDocument(String json) /*-{
		return eval(json);
	}-*/;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		MyTreeWidget treeWidget = new MyTreeWidget();
		
		final TextArea textArea = new TextArea();
		textArea.setSize(800, 300);

		treeWidget.getView().setBroadcastCommand(new BroadcastCommand()
		{

			@Override
			public void broadcast(String jsonMsg)
			{
				String value = textArea.getValue();
				if(value == null)
				{
					value = "";
				}
				
				textArea.setValue(value + jsonMsg + "\n");
			}
		});

		Viewport viewport = new Viewport();
		viewport.add(treeWidget, new MarginData(10));
		viewport.add(textArea, new MarginData(10));
		RootPanel.get().add(viewport);
	}
}
