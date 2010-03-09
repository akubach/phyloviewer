package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.Job;
import org.iplantc.iptol.client.events.JobStatusChangeEvent;
import org.iplantc.iptol.client.events.JobStatusChangeEventHandler;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class JobStatusPanel extends VerticalPanel 
{
	//////////////////////////////////////////
	//private types
	private enum ViewState 
	{
		VIEW,
		VIEW_ERRORS,
		VIEW_RESULTS
	}
	
	//////////////////////////////////////////
	//private variables
	private Button btnStart;
	private Button btnStop;
	private Button btnEdit;
	private Button btnView;
	
	private String caption;
	private int idxRowSelected = -1;
	private ViewState stateView = ViewState.VIEW;
	
	private Grid<Job> grid;
	ColumnModel columnModel;
	
	private HandlerManager eventbus;
	private List<Job> jobs;
	private String selectedJob = new String();
	
	//////////////////////////////////////////
	//constructor
	public JobStatusPanel(HandlerManager eventbus,String caption)
	{
		this.eventbus = eventbus;
		this.caption = caption;
		
		//register perspective change handler
		eventbus.addHandler(JobStatusChangeEvent.TYPE,new JobStatusChangeEventHandler()
        {        	
			@Override
			public void onStatusChange(JobStatusChangeEvent event) 
			{
				jobs = event.getJobs();
				updateGrid();				
			}
        });		
	}
	
	//////////////////////////////////////////
	//private methods
	private Button buildButton(String caption,boolean enabled,SelectionListener<ButtonEvent> listener)
	{
		Button ret = new Button(caption,listener);
		
		ret.setMinWidth(80);
		ret.setEnabled(enabled);
		
		return ret;
	}
	
	//////////////////////////////////////////
	private ColumnConfig buildColumn(String id,String header,int width)
	{
		GridCellRenderer<Job> cellRender = new GridCellRenderer<Job>() 
		{  
			@Override
			public Object render(Job model, String property, ColumnData config,
					int rowIndex, int colIndex, ListStore<Job> store,
					Grid<Job> grid) 
			{
				String style = (model.get("status").equals("Error")) ? "red" : "black";
				
				return "<span style='color:" + style + "'>" + model.get(property) + "</span>";			
			}  
		};
		
		ColumnConfig ret = new ColumnConfig(id,header,width);
		ret.setMenuDisabled(true);
		ret.setRenderer(cellRender);  
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void setViewState(String status)
	{
		if(status.equals("Complete"))
		{
			stateView = ViewState.VIEW_RESULTS;
		}
		else if(status.equals("Error"))
		{
			stateView = ViewState.VIEW_ERRORS;
		}
		else 
		{
			stateView = ViewState.VIEW;
		}
	}
	
	//////////////////////////////////////////
	private void updateViewButtonCaption()
	{
		String caption = "View";
		
		switch(stateView)
		{
			case VIEW_ERRORS:
				caption = "View Errors";
				break;
			
			case VIEW_RESULTS:
				caption = "View Results";
				break;
			
			default:
				break;
		}
		
		btnView.setText(caption);
	}
	
	//////////////////////////////////////////
	private void updateButtons()
	{
		Job job = jobs.get(idxRowSelected);
		
		if(job != null)
		{
			String status = job.get("status");
						
			if(status.equals("Processing"))
			{
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				btnEdit.setEnabled(false);
				btnView.setEnabled(false);
			}
			else if(status.equals("Not Started") || status.equals("Stopped"))
			{
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				btnEdit.setEnabled(true);
				btnView.setEnabled(false);
			}						
			else
			{
				btnStart.setEnabled(false);
				btnStop.setEnabled(false);
				btnEdit.setEnabled(true);
				btnView.setEnabled(true);			
			}					
			
			setViewState(status);
			updateViewButtonCaption();
		}
	}
			
	//////////////////////////////////////////
	private ColumnModel buildColumnModel()
	{
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		 
		configs.add(buildColumn("id","Id",100));		   
		configs.add(buildColumn("name","Name",400));  			   
		configs.add(buildColumn("status","Status",150));		   
		
		return new ColumnModel(configs);		 
	}
	
	//////////////////////////////////////////
	private Grid<Job> buildJobStatusTable()
	{
		ListStore<Job> store = new ListStore<Job>();  
		store.add(jobs);  
				
		Grid<Job> ret = new Grid<Job>(store,columnModel);  
		ret.setStyleAttribute("borderTop","none");  
		ret.setBorders(true);  
		ret.setStripeRows(true);
		ret.setAutoHeight(true);
		ret.setWidth(656);
		ret.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			
		Listener<GridEvent<Job>> gridListener = new Listener<GridEvent<Job>>() 
		{
			public void handleEvent(GridEvent<Job> be) 
			{
				EventType type = be.getType();

				if(type == Events.CellClick) 
				{
					idxRowSelected = be.getRowIndex(); 
					updateButtons();
				}
			}
		};
		
		ret.addListener(Events.CellClick,gridListener);
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void setCurrentlySelectedJobStatus(String status)
	{
		Job job = jobs.get(idxRowSelected);
		
		if(job != null)
		{
			job.set("status",status);
			
			ListStore<Job> store = new ListStore<Job>();  
			store.add(jobs);  
			grid.reconfigure(store,columnModel);
			
			updateButtons();
		}	
	}
	
	//////////////////////////////////////////
	private void startCurrentlySelectedJob()
	{
		setCurrentlySelectedJobStatus("Processing");
	}
	
	//////////////////////////////////////////
	private void stopCurrentlySelectedJob()
	{
		setCurrentlySelectedJobStatus("Stopped");
	}
		
	//////////////////////////////////////////
	private void editJob()
	{
		MessageBox.alert("Coming soon!","Job editing functionality.",null);
	}
	
	//////////////////////////////////////////
	private void viewErrors()
	{
		MessageBox.alert("Coming soon!","Error Viewing funcitonality.",null);
	}
	
	//////////////////////////////////////////
	private void viewResults()
	{
		MessageBox.alert("Coming soon!","Results Viewing funcitonality.",null);
	}
	
	//////////////////////////////////////////
	private void doView()
	{
		if(stateView == ViewState.VIEW_ERRORS)
		{
			viewErrors();
		}
		else if(stateView == ViewState.VIEW_RESULTS)
		{
			viewResults();
		}
	}
				
	//////////////////////////////////////////
	private Widget buildButtonPanel()
	{
		VerticalPanel ret = new VerticalPanel();
		ret.setSpacing(10);
				
		//build start button
		btnStart = buildButton("Start",false,new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				startCurrentlySelectedJob();				
			}			
		});
		
		//build stop button
		btnStop = buildButton("Stop",false,new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				stopCurrentlySelectedJob();
			}			
		});
		
		//build edit button
		btnEdit = buildButton("Edit",false,new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				editJob();								
			}			
		});
		
		//build view button
		btnView = buildButton("View",false,new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doView();				
			}			
		});
		
		ret.add(btnStart);
		ret.add(btnStop);
		ret.add(btnEdit);
		ret.add(btnView);
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void initJobStatusTable()
	{
		columnModel = buildColumnModel();		
		grid = buildJobStatusTable();			
	}
		
	//////////////////////////////////////////
	private Widget buildLayoutPanel()
	{
		HorizontalPanel ret = new HorizontalPanel();
	
		initJobStatusTable();
		ret.add(grid);
		ret.add(buildButtonPanel());
		
		return ret;		
	}
		
	//////////////////////////////////////////
	private void doPanelLayout()
	{
		Widget panelLayout = buildLayoutPanel();
		
		VerticalPanel panelInner = new VerticalPanel();
		panelInner.setSpacing(10);
		panelInner.add(panelLayout);
		
		FormLayout layout = new FormLayout();  
		layout.setLabelWidth(75);  
				
		ContentPanel panel = new ContentPanel();
		panel.setHeading(caption);		
		panel.add(panelInner);
		add(panel);
		selectJob(selectedJob);
		
		layout();
	}
	
	//////////////////////////////////////////
	private void updateGrid()
	{
		ListStore<Job> store = grid.getStore();
		store.removeAll();
		store.add(jobs);
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent, int index) 
	{  
		super.onRender(parent, index);
		
		//TODO: get jobs from service
		
		/*RPCFacade.getJobs(new AsyncCallback<List<Job>>() 
		{			
			@Override
			public void onSuccess(List<Job> result) 
			{
				jobs = result;
				doPanelLayout();
			}
			
			@Override
			public void onFailure(Throwable caught) 
			{
				// TODO: handle failure				
			}
		}); */		
	}
	
	//////////////////////////////////////////
	//public methods
	public void selectJob(String id) 
	{
		if(grid != null && id.length() > 0)
		{
			Store<Job> store = grid.getStore();
			List<Job> jobs = store.getModels();
			
			int idxRow = 0;
			for(Job job : jobs)
			{
				String testId = job.get("id");

				//did we find the desired row?
				if(testId.equals(id))
				{
					idxRowSelected = idxRow;
					grid.getSelectionModel().select(grid.getStore().getAt(idxRowSelected),true);
					updateButtons();
					break;
				}
				
				idxRow++;
			}
			
			selectedJob = "";
		}	
		else
		{
			selectedJob = id;
		}
	}	
}
