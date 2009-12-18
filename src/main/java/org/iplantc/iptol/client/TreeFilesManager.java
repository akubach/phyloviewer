package org.iplantc.iptol.client;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.JsonReader;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Provides UI to upload files and list the uploaded files in a grid
 */
public class TreeFilesManager extends VerticalPanel {

	public static final String OPERATIONS_RADIO_GROUP = "operationsRadioGroup";
	public static final int HSPACING = 20;
	public static final int VSPACING = 10;
	public static final String FILE_NAME = "File Name";
	public static final String LABEL = "Label";
	public static final String DATE_TIME = "Uploaded Date/Time";
	public static final String UPLOADED_TREES = "Uploaded Data";
	public static final int TREE_DATA_GRID_HEIGHT = 250;
	public static final int TREE_DATA_GRID_WIDTH = 400;
	public static final String UPLOAD_PROMPT = "Upload your data";
	public static final String SERVLET_PATH = "servlet.gupld";
	public static final String DESCRIPTION = "Description";
	public static final String LOCAL_FILE_PATH ="Local Path";

	private IptolConstants constants = (IptolConstants) GWT
			.create(IptolConstants.class);

	private ListStore<ModelData> store = null;
	private Grid<ModelData> grid = null;
	private ModelType type = new ModelType();
	private ContentPanel hOperations_panel;
	
	private String jsonResult = null;
	private PagingToolBar pagingToolBar =null;
	private BasePagingLoader<PagingLoadResult<ModelData>> loader = null;
	private ArrayList<ModelData> model = null;
	

	public void assembleComponents() {
		//upload panel 	
		UploadPanel upload_panel = new UploadPanel(UPLOAD_PROMPT, SERVLET_PATH, onFinishUploaderHandler);
		upload_panel.assembleComponents();
		
		// call service to return a list of files uploaded
		getTreeFilesInfo();
		
		//configure data browser grid
		configureGrid();

		ContentPanel panel = new ContentPanel();
		panel.setFrame(true);
		panel.setWidth(TREE_DATA_GRID_WIDTH);
		panel.setCollapsible(true);
		panel.setAnimCollapse(false);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		panel.setHeading(UPLOADED_TREES);
		panel.setLayout(new FitLayout());
		panel.add(grid);
		panel.setSize(TREE_DATA_GRID_WIDTH, TREE_DATA_GRID_HEIGHT);
		panel.setBottomComponent(pagingToolBar);

		buildOperationsPanel();
		
		this.add(upload_panel);
		this.add(panel);
		this.add(hOperations_panel);
		this.setSpacing(VSPACING);
	}
	// prepare the grid
	@SuppressWarnings("unused")
	private void configureGrid() {
		
		
		JsonReader<ModelData> reader = null;
		
		//render green/orange/red buttons for parsing status
		GridCellRenderer<ModelData> statusRenderer = new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				String status = (String) model.get("Status");
				return new Image("./discoveryenvironment/images/Green.png");
			}
			
		};
		
		GridCellRenderer<ModelData> toolTipRenderer = new GridCellRenderer<ModelData>() {
			 public String render(ModelData model, String property, ColumnData config, int rowIndex,
		                int colIndex, ListStore<ModelData> store,Grid<ModelData> grid) {
		            String prop = model.get("Description");
		            return "<span qtip='" + prop + "'>" + model.get(property) +"</span>";
		        }
		};
		
		
		// column config for tables
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		ColumnConfig statusColumn = new ColumnConfig("Status",25);
		statusColumn.setRenderer(statusRenderer);
		columns.add(statusColumn);
		
		ColumnConfig fileNameColumn = new ColumnConfig(FILE_NAME, FILE_NAME, 100);
		fileNameColumn.setRenderer(toolTipRenderer);
		columns.add(fileNameColumn);
		
		
		ColumnConfig labelColumn = new ColumnConfig(LABEL, LABEL, 100);
		labelColumn.setRenderer(toolTipRenderer);
		columns.add(labelColumn);
		
		ColumnConfig dateColumn = new ColumnConfig(DATE_TIME, DATE_TIME, 100);
		dateColumn.setRenderer(toolTipRenderer);
		columns.add(dateColumn);
		
		// create the column model
		ColumnModel column_model = new ColumnModel(columns);

		// defines the xml structure
		type.setRoot("data");
		type.addField("Status");
		type.addField(FILE_NAME);
		type.addField(LABEL);
		type.addField(DATE_TIME);
		type.addField("Description");
		
		//json result mock
		jsonResult = "{\"data\":[{\"Status\":\"Ready\",\"Description\":\"A Nexus file with tree\",\"File Name\":\"basic.nex\",\"Uploaded Date/Time\":\"Fri Dec 18 10:33:45 MST 2009\",\"Label\":\"basic bush\"}]}";
		//enable tool tip for grid
		QuickTip tip = new QuickTip(grid);
		
		reader = new JsonReader<ModelData>(type);
		
		if(reader!=null) {
			 model = (ArrayList<ModelData>) reader.read(null, jsonResult);
		}
//		
//		// add paging support for a local collection of models  
		PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(model);  
//		
//		//set loader for loading json data into grid
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);  
		loader.setRemoteSort(true);  
		
		store = new ListStore<ModelData>(loader);
		grid = new DataBrowserGrid(store, column_model);
		grid.setAutoExpandColumn(DATE_TIME);
		
		pagingToolBar = new PagingToolBar(10);
		pagingToolBar.bind(loader);
		loader.load(0, 10);
		
		
		
		grid.addListener(Events.RowClick, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				 GridEvent ge = (GridEvent)be;
	                if(ge.getRowIndex() >= 0)
	                {
	                	ModelData data = grid.getStore().getAt(ge.getRowIndex());
	                	Window.alert(""+data.get(FILE_NAME));
	                }
			}
			
		});
		
		
		
	}
	
	//build operations panel
	private void buildOperationsPanel() {
		hOperations_panel = new ContentPanel();
		hOperations_panel.setHeight(35);
		hOperations_panel.setWidth(350);
		hOperations_panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		hOperations_panel.add(new RadioButton(OPERATIONS_RADIO_GROUP,
				"View Tree"),new RowData(116,35));
		hOperations_panel.add(new RadioButton(OPERATIONS_RADIO_GROUP,
				"Download"),new RowData(116,35));
		hOperations_panel
				.add(new RadioButton(OPERATIONS_RADIO_GROUP, "Delete"),new RowData(116,35));
		hOperations_panel.setFrame(true);
		hOperations_panel.setHeaderVisible(false);
		hOperations_panel.setBorders(false);
	}

	/**
	 * Call back method for file upload submit
	 */
	public IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		@SuppressWarnings("unchecked")
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				String response = uploader.getServerResponse();
				JsonReader<ModelData> reader = new JsonReader<ModelData>(type);
				if(reader!=null) {
					ArrayList record = (ArrayList) reader.read(null, response);
					model.addAll(record);
					store.add(model);
					loader.load(0,10);
				}
				Info.display("File Upload", constants.fileUploadSuccess());
			} else {
				MessageBox.alert("File Upload", constants.fileUploadFailed(),null);
			}
		}
	};
	/**
	 * Private method to retrieve list of all uploaded files
	 */
	private void getTreeFilesInfo() {
		IptolServiceFacade.getInstance().getServiceData(
				constants.treeFilesListService(),
				new TreeFilesListUpdater<String>());
	}

	/**
	 * 
	 * @author sriram
	 * @param <String>
	 * Callback for treeFilesListService
	 */
	@SuppressWarnings("hiding")
	class TreeFilesListUpdater<String> extends AbstractAsyncHandler {

		@Override
		public void handleFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void handleSuccess(Object result) {
			//jsonResult = (java.lang.String) result;
			jsonResult = "{\"data\":[{\"Status\":\"Ready\",\"Description\":\"A Nexus file with tree\",\"File Name\":\"basic.nex\",\"Uploaded Date/Time\":\"Fri Dec 18 10:33:45 MST 2009\",\"Label\":\"basic bush\"}]}";
		}
	}
}
