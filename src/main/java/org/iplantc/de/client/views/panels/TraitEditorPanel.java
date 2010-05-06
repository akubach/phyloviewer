package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.List;
import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.FileEditorWindowDirtyEvent;
import org.iplantc.de.client.services.TraitServices;
import org.iplantc.de.client.utils.TraitDataJsonGen;
import org.iplantc.de.client.utils.TraitDataJsonParser;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TraitEditorPanel extends ContentPanel{

	private boolean dirty;
	private String id;
	private String idFile;
	
	private EditorGrid<ModelData> grid;
	private TraitDataJsonParser parser;
	private List<ColumnConfig> columns;
	private ListStore<ModelData> store = null;

	private ModelType type;

	private JsonLoadResultReader<ListLoadResult<ModelData>> reader;
	
	private Button save;
	
	DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);

	
	public TraitEditorPanel(String id,String idFile,String json) {
		this.id = id;
		this.idFile = idFile;		
		parser = new TraitDataJsonParser(json);
		columns = new ArrayList<ColumnConfig>();
		assembleView();
	}

	public void parseJson() {
		parser.parseRoot();
	}

	private void assembleView() {
		loadData();
		this.setScrollMode(Scroll.NONE);
		this.setHeaderVisible(true);
		this.setHeaderVisible(false);
		this.setLayout(new FitLayout());
		this.add(grid);
		this.setTopComponent(buildToolBar());
		
	}

	private ToolBar buildToolBar() {
		ToolBar toolBar = new ToolBar();

		DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
		
		save = new Button(displayStrings.save());
		toolBar.add(save);
		save.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				store.commitChanges();
				save.setEnabled(false);
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
					FileEditorWindowDirtyEvent event = new FileEditorWindowDirtyEvent(idFile,true);
					eventbus.fireEvent(event);
				}
			}
		});
	}
	
	class TraitDataSaveCallBack implements AsyncCallback<String> {

		@Override
		public void onFailure(Throwable caught) {
			save.setEnabled(true);
			DEErrorStrings errorStrings = (DEErrorStrings) GWT.create(DEErrorStrings.class);
			ErrorHandler.post(errorStrings.rawDataSaveFailed());
		}

		@Override
		public void onSuccess(String result) {
			//Window.alert("saved");			
			dirty = false;			
			save.setEnabled(true);
			Info.display("Save", displayStrings.fileSave());
			EventBus eventbus = EventBus.getInstance();							
			FileEditorWindowDirtyEvent event = new FileEditorWindowDirtyEvent(idFile,false);
			eventbus.fireEvent(event);		
		}		
	}

	///////////////////////////////////////
	public boolean isDirty()
	{
		return dirty;
	}
}
