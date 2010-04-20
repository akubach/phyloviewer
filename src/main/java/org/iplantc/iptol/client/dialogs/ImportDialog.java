package org.iplantc.iptol.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.JsonBuilder;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.models.Taxon;
import org.iplantc.iptol.client.models.TaxonInfo;
import org.iplantc.iptol.client.services.FolderServices;
import org.iplantc.iptol.client.services.ImportServices;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportDialog extends Dialog 
{
	//////////////////////////////////////////
	//private variables
	private String idFolder;
	private String idWorkspace;
	private HorizontalPanel panelSearch;
	private Grid<Taxon> grid;
	private Status status; 
	private MessageBox fileNamePrompt;
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	private Button searchBtn;
	//////////////////////////////////////////
	//constructor
	public ImportDialog(Point p,String idWorkspace,String idFolder)
	{
		this.idWorkspace = idWorkspace;
		this.idFolder = idFolder;
		
		setup(p);
		
		panelSearch = buildSearchPanel();
		grid = buildEditorGrid();
		
		fileNamePrompt = MessageBox.prompt("File Name", displayStrings.fileName()); 
		fileNamePrompt.close();
		
		initButtons();		
	}
	
	//////////////////////////////////////////
	//private methods
	private final native JsArray<TaxonInfo> asArrayofTaxonInfo(String json) /*-{
		return eval(json);
	}-*/;

	//////////////////////////////////////////
	private void setup(Point p)
	{
		setHeading(displayStrings.importPhylota());
		setPagePosition(p);
		setWidth(445);
		setResizable(false);
		setModal(true);
		setButtons(Dialog.OKCANCEL);	
	}
	
	//////////////////////////////////////////
	private void initButtons()
	{
		Button btn = getButtonById("ok");
		btn.setText(displayStrings.tagImport());
		btn.addSelectionListener(new SelectionListener<ButtonEvent>() 
	    {
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				Taxon selected = grid.getSelectionModel().getSelectedItem();
				if(selected == null)
				{
					MessageBox.alert(errorStrings.error(),errorStrings.mustSelectBeforeImport(),null);	
				}
				else
				{
					doImport(selected);										
				}							
			}			
		});		
		
		btn = getButtonById("cancel");
		btn.addSelectionListener(new SelectionListener<ButtonEvent>() 
	    {
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				hide();						
			}			
		});
	}
	
	//////////////////////////////////////////
	private TextField<String> buildSearchField()
	{
		final TextField<String> ret = new TextField<String>();
		ret.setFieldLabel(displayStrings.taxonName());	
		ret.setSelectOnFocus(true);
		ret.focus();
		
		ret.addKeyListener(new KeyListener()
		{
			public void componentKeyUp(ComponentEvent event)
			{
				if(event.getKeyCode() == KeyCodes.KEY_ENTER)
				{
					doSearch(ret.getValue());
				}
			}
		});
		
		return ret; 	
	}

	//////////////////////////////////////////
	private void updateStore(String json)
	{
		JsArray<TaxonInfo> taxonInfos = asArrayofTaxonInfo(json);
		ListStore<Taxon> store = grid.getStore();
		store.removeAll();
				
		for(int i = 0;i < taxonInfos.length();i++)
		{
			store.add(new Taxon(taxonInfos.get(i)));
		}	
	}
	
	//////////////////////////////////////////
	private void doSearch(String nameTaxon)
	{
		if(nameTaxon != null)
		{
			nameTaxon = nameTaxon.trim();
			
			if(nameTaxon.length() > 0)
			{
				//let's give the user some feedback that we are searching
				status.show();
				status.setBusy("");
				searchBtn.setEnabled(false);
				
				ImportServices.getSearchResults(nameTaxon,new AsyncCallback<String>()
				{
					@Override
					public void onFailure(Throwable arg0) 
					{
						status.clearStatus("");
						ErrorHandler.post(errorStrings.searchFailed());		
						searchBtn.setEnabled(true);
					}

					@Override
					public void onSuccess(String result) 
					{
						status.clearStatus("");
						updateStore(result);
						searchBtn.setEnabled(true);
						layout();
					}					
				});
			}
		}		
	}
	
	//////////////////////////////////////////
	private void buildSearchButton(final TextField<String> field)
	{
		searchBtn = new Button(displayStrings.search());
		searchBtn.addSelectionListener(new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{			
				doSearch(field.getValue());  
			}
		});	    	
		searchBtn.setEnabled(false);
		
	}
	
	//////////////////////////////////////////
	private HorizontalPanel buildSearchPanel()
	{
		HorizontalPanel ret = new HorizontalPanel();
		FormPanel panelField = new FormPanel();
		panelField.setHeaderVisible(false);
		panelField.setBorders(false);
		panelField.setBodyBorder(false);
		
		TextField<String> searchField = buildSearchField(); 
		searchField.setMaxLength(128);	
	
		panelField.add(searchField);		
		ret.add(panelField);
		
		HorizontalPanel panelBtn = new HorizontalPanel();
		panelBtn.setStyleAttribute("padding-top","10px");
		buildSearchButton(searchField);
		panelBtn.add(searchBtn);
				
		status = new Status();
		panelBtn.add(status);
		status.hide();
		
		searchField.addListener(Events.OnKeyUp,new Listener<FieldEvent>() 
		{
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(FieldEvent be) 
			{
				TextField<String> field = (TextField<String>) be.getSource();
				if(field.getValue()!=null && field.getValue().length() >= 3) 
				{
					searchBtn.setEnabled(true);
				} 
				else 
				{
					searchBtn.setEnabled(false);
				} 			
			}
		});		
		
		ret.add(panelBtn);		
		
		return ret;
	}
	
	//////////////////////////////////////////
	private ColumnConfig buildColumn(String id,String header,int width)
	{
		ColumnConfig ret = new ColumnConfig(id,header,width);
		ret.setMenuDisabled(true);
		
		return ret;
	}
	
	//////////////////////////////////////////
	private ColumnModel buildColumnHeaders()
	{
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		columns.add(buildColumn("clusterId",displayStrings.cluster(),80));  
		columns.add(buildColumn("taxonId",displayStrings.taxonId(),80));  
		columns.add(buildColumn("taxonName",displayStrings.name(),250));  
		
		return new ColumnModel(columns);		
	}

	//////////////////////////////////////////
	private void doImport(final Taxon taxon)
	{
		if(taxon != null)
		{
			//first we need to get the data for this taxon
			ImportServices.getTree(taxon.getTaxonId(), taxon.getClusterId(),new AsyncCallback<String>()
			{
				@Override
				public void onFailure(Throwable arg0) 
				{
					ErrorHandler.post(errorStrings.treeServiceRetrievalFailed());					
				}

				@Override
				public void onSuccess(final String result) 
				{
					fileNamePrompt.show();
					fileNamePrompt.addCallback(new Listener<MessageBoxEvent>() {  
						public void handleEvent(MessageBoxEvent be) {  
							  checkDuplicateFile(be.getValue(),result);
						}  
					});  
				}				
			});
		}
	}
		
	 private void checkDuplicateFile(final String fileName, final String result) {
		FolderServices.getListofFiles(idWorkspace, new AsyncCallback<String>() {
			boolean flag = false;
			
			 final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {  
			       public void handleEvent(MessageBoxEvent ce) {  
			         com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();  
			         if(btn.getText().equals("Yes")) {
			        	 uploadFile(fileName, result);
			         } else {
			        	 fileNamePrompt.show();
			         }
			       }  
			 };  
			 
			@Override
			public void onSuccess(String result) {
				JsArray<FileInfo> fileinfos = asArrayofFileData(result);
				for (int i = 0; i < fileinfos.length(); i++) {
					if(fileinfos.get(i).getName().equals(fileName)) {
						flag = true;
						MessageBox.confirm("Duplicate File", displayStrings.duplicateFile(), l);
						break;
					}
				}
				
				if (!flag) {
					uploadFile(fileName, result);
				}
				
			}
		
			
			@Override
			public void onFailure(Throwable caught) {
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());
				
			}
		});
		
		
	 }
	 
	private final native JsArray<FileInfo> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;
	
	private void uploadFile(String filename, String result) {
		FolderServices.uploadFile(idWorkspace,filename,idFolder,result,new AsyncCallback<String>()
				{
					@Override
					public void onFailure(Throwable arg0) 
					{
						ErrorHandler.post(errorStrings.importFailed());							
					}

					@Override
					public void onSuccess(String response) 
					{
						if(response != null)
						{	
							JSONObject obj = JSONParser.parse(response).isObject();
							JsArray<FileInfo> fileInfos = JsonBuilder.asArrayofFileData(obj.get("created").toString());
							ArrayList<String> deleteIds = null;
							JSONArray arr = null; 
							
							if(obj.get("deletedIds")!=null ) {
								arr = obj.get("deletedIds").isArray();
							}
							
							StringBuffer sb = null;
							if(arr!=null) {
								deleteIds = new ArrayList<String>();
								//remove sorrounding quotes
								for (int i=0;i<arr.size();i++) {
									sb = new StringBuffer(arr.get(i).toString());
									sb.deleteCharAt(0);
									sb.deleteCharAt(sb.length() - 1);
									deleteIds.add(sb.toString());
								}
							}
							//there is always only one record
							if(fileInfos != null)
							{
								FileInfo info = fileInfos.get(0);
								
								if(info != null)
								{
									EventBus eventbus = EventBus.getInstance();
									FileUploadedEvent event = new FileUploadedEvent(idFolder,info,deleteIds );							
									eventbus.fireEvent(event);
									Info.display(displayStrings.fileUpload(),displayStrings.fileUploadSuccess());						
								}						
							}				
						}	
						
						hide();
					}						
				});
	}
	//////////////////////////////////////////
	private Grid<Taxon> buildEditorGrid()
	{			
		ColumnModel cm = buildColumnHeaders();		
		
		Grid<Taxon> ret = new Grid<Taxon>(new ListStore<Taxon>(),cm);		
		setBorders(true);  
		ret.setHeight(200);
		ret.setStripeRows(true);
		ret.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		ret.getView().setEmptyText(displayStrings.noItemsToDisplay());
				
		return ret;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		add(panelSearch);		
		add(grid);
	}	
}

