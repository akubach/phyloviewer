package org.iplantc.iptol.client.dialogs;

import java.util.ArrayList;
import java.util.List;
import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.models.Taxon;
import org.iplantc.iptol.client.models.TaxonInfo;
import org.iplantc.iptol.client.services.ImportServices;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportDialog extends Dialog 
{
	//////////////////////////////////////////
	//private variables
	private String idParentFolder;
	
	private HorizontalPanel panelSearch;
	private Grid<Taxon> grid;
	
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public ImportDialog(Point p,String idParentFolder)
	{
		this.idParentFolder = idParentFolder;
		setHeading(displayStrings.importPhylota());
		setPagePosition(p);
		setWidth(445);
		setResizable(false);
		setModal(true);
		this.setButtons(Dialog.OKCANCEL);
		
		panelSearch = buildSearchPanel();
		grid = buildEditorGrid();		
	}
		
	//////////////////////////////////////////
	//private methods
	private final native JsArray<TaxonInfo> asArrayofTaxonInfo(String json) /*-{
		return eval(json);
	}-*/;

	//////////////////////////////////////////
	private TextField<String> buildSearchField()
	{
		TextField<String> ret = new TextField<String>();
		ret.setFieldLabel(displayStrings.taxonName());	
		ret.setSelectOnFocus(true);
		ret.focus();
		
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
				ImportServices.getSearchResults(nameTaxon, new AsyncCallback<String>()
				{
					@Override
					public void onFailure(Throwable arg0) 
					{
						ErrorHandler.post(errorStrings.searchFailed());						
					}

					@Override
					public void onSuccess(String result) 
					{
						updateStore(result);
						layout();
					}					
				});
			}
		}		
	}
	
	//////////////////////////////////////////
	private Button buildSearchButton(final TextField<String> field)
	{
		Button ret = new Button(displayStrings.search(),new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{			
				doSearch(field.getValue());  
			}
		});	    	
		
		return ret;
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
		
		VerticalPanel panelBtn = new VerticalPanel();
		panelBtn.setStyleAttribute("padding-top","10px");
		panelBtn.add(buildSearchButton(searchField));
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
	private void doImport(Taxon taxon)
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
				public void onSuccess(String result) 
				{
					String temp = result;
					
					temp += "";
				}				
			});
		}
	}
	
	//////////////////////////////////////////
	private ToolBar buildToolbar() 
	{
		ToolBar ret = new ToolBar();
		
		ret.add(new FillToolItem());
		
		Button btnImport = new Button(displayStrings.tagImport(),new SelectionListener<ButtonEvent>() 
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
		
		ret.add(btnImport);
		
		return ret;
	}
	
	//////////////////////////////////////////
	private Grid<Taxon> buildEditorGrid()
	{			
		ColumnModel cm = buildColumnHeaders();		
		
		Grid<Taxon> ret = new Grid<Taxon>(new ListStore<Taxon>(),cm);		
		setBorders(true);  
		ret.setHeight(200);
		ret.setStripeRows(true);
		//ret.setAutoHeight(true);
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
		add(buildToolbar());
	}	
}
