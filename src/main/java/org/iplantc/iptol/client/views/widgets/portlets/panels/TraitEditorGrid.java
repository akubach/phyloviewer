package org.iplantc.iptol.client.views.widgets.portlets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.events.FileEditorPortletDirtyEvent;
import org.iplantc.iptol.client.events.FileEditorPortletSavedEvent;
import org.iplantc.iptol.client.services.TraitServices;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TraitEditorGrid {

	private boolean dirty;
	private String id;
	private String idFile;
	private ContentPanel panel;
	private EditorGrid<ModelData> grid;
	private TraitDataJsonParser parser;
	private List<ColumnConfig> columns;
	private ListStore<ModelData> store = null;

	private ModelType type;

	private JsonLoadResultReader<ListLoadResult<ModelData>> reader;
	private ArrayList<ModelData> model = null;
	private PagingToolBar pagingToolBar = null;
	private BasePagingLoader<PagingLoadResult<ModelData>> loader = null;
	

	public TraitEditorGrid(String id,String idFile,String json) {
		this.id = id;
		this.idFile = idFile;		
		parser = new TraitDataJsonParser(json);
		columns = new ArrayList<ColumnConfig>();
	}

	public void parseJson() {
		parser.parseRoot();
	}

	public ContentPanel assembleView() {
		loadData();
		panel = new ContentPanel();

		panel.setScrollMode(Scroll.NONE);
		panel.setHeaderVisible(true);
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());

		panel.add(grid);
		panel.setTopComponent(buildToolBar());
		panel.setBottomComponent(pagingToolBar);

		return panel;
	}

	private ToolBar buildToolBar() {
		ToolBar toolBar = new ToolBar();

		// Button add = new Button("Add");
		// toolBar.add(add);
		// add.addSelectionListener(new SelectionListener<ButtonEvent>() {
		// @SuppressWarnings("unchecked")
		// @Override
		// public void componentSelected(ButtonEvent ce) {
		// // create a mock row and add
		// // ArrayList<ModelData> data = (ArrayList<ModelData>) reader.read(
		// // null, (TraitDataJsonParser
		// // .getMockUp(parser.getHeader())).toString());
		// //
		// // grid.stopEditing(false);
		// // store.insert((ModelData)data.get(0), 0);
		// // grid.startEditing(0,0);
		// }
		// });
		IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
		
		Button save = new Button(displayStrings.save());
		toolBar.add(save);
		save.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				store.commitChanges();
				TraitDataJsonGen gen = new TraitDataJsonGen(store.getModels(), parser.getHeader().isArray());
				TraitServices.saveMatrices(id,gen.generateJson() ,new TraitDataSaveCallBack());
			}
		});

		Button reset = new Button(displayStrings.reset());
		toolBar.add(reset);
		reset.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				store.rejectChanges();
				dirty = false;	
			}
		});

		// Button delete = new Button("Delete");
		// toolBar.add(delete);
		// delete.addSelectionListener(new SelectionListener<ButtonEvent>() {
		// @Override
		// public void componentSelected(ButtonEvent ce) {
		// for (ModelData data : grid.getSelectionModel().getSelectedItems()) {
		// store.remove(data);
		// }
		// }
		//			
		//			
		// });
		return toolBar;
	}

	@SuppressWarnings("unchecked")
	private void loadData() {
		this.parseJson();
		ColumnConfig column = null;
		JSONArray headers = parser.getHeader().isArray();
		// create modeltype
		type = new ModelType();
		type.setRoot("data");
		type.addField("id", "id");

		CheckBoxSelectionModel<ModelData> checkbox = new CheckBoxSelectionModel<ModelData>();
		columns.add(checkbox.getColumn());

		TextField<String> text = new TextField<String>();
		text.setAllowBlank(false);
		CellEditor editor = new CellEditor(text);

		for (int i = 0; i < headers.size(); i++) {
			JSONValue val = headers.get(i);
			JSONObject obj = val.isObject();
			column = new ColumnConfig(TraitDataJsonParser.trim(obj.get("id")
						.toString()), TraitDataJsonParser.trim(obj.get("label").toString()), 100);
		
			// cannot edit species name for now
			if (i != 0) {
				column.setEditor(editor);
			}

			if(TraitDataJsonParser.trim(obj.get("label").toString()).equals("")) {
				type.addField(TraitDataJsonParser.trim(TraitDataJsonParser.trim(obj
						.get("id").toString())),TraitDataJsonParser.makeUpEmptyHeader(i));
						
			} else {
				type.addField(TraitDataJsonParser.trim(TraitDataJsonParser.trim(obj
						.get("id").toString())),TraitDataJsonParser.trim(obj.get("label").toString()));
			}
			
			columns.add(column);
			
			column = null;
		}

		// create the column model
		ColumnModel cm = new ColumnModel(columns);

		reader = new JsonLoadResultReader<ListLoadResult<ModelData>>(type);  

//		if (reader != null) {
//			model = (ArrayList<ModelData>) reader.read(null, parser.getData()
//					.toString());
//		}

		// add paging support for a local collection of models
//		PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(model);
//		//			
//		// //set loader for loading json data into grid
//		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
//		loader.setRemoteSort(true);
//
//		store = new ListStore<ModelData>(loader);
//		grid = new EditorGrid<ModelData>(store, cm);
//
//		grid.addPlugin(checkbox);
//		grid.setSelectionModel(checkbox);
//		grid.setBorders(true);
//
//		pagingToolBar = new PagingToolBar(10);
//
//		pagingToolBar.bind(loader);
//
//		loader.load(0, 10);
		
		MemoryProxy<ModelData> proxy = new MemoryProxy<ModelData>(reader.read(null, parser.getData().toString()));
		final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
		store = new ListStore<ModelData>(loader);
		grid = new EditorGrid<ModelData>(store, cm);
		loader.load();
		grid.getStore().addStoreListener(new StoreListener<ModelData>()
		{
			@Override
			public void storeUpdate(StoreEvent<ModelData> se) 
			{
				if(!dirty)
				{
					dirty = true;		
					EventBus eventbus = EventBus.getInstance();							
					FileEditorPortletDirtyEvent event = new FileEditorPortletDirtyEvent(idFile);
					eventbus.fireEvent(event);
				}
			}
		});
	}
	
	class TraitDataSaveCallBack implements AsyncCallback<String> {

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess(String result) {
			//Window.alert("saved");			
			dirty = false;			
			EventBus eventbus = EventBus.getInstance();							
			FileEditorPortletSavedEvent event = new FileEditorPortletSavedEvent(idFile);
			eventbus.fireEvent(event);		
		}		
	}

	///////////////////////////////////////
	public boolean isDirty()
	{
		return dirty;
	}
}
