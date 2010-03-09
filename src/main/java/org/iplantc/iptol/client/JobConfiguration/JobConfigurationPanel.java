package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.BufferView;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;

/**
 * A widget that renders job configuration wizard
 * 
 * @author sriram
 * 
 */
public class JobConfigurationPanel extends ContentPanel {

	private Grid<JobStep> grid;

	private Dialog popup;
	private ListStore<JobStep> store;
	private ContentPanel navpanel;
    private HandlerManager eventbus;
	private JobView icj;
	
	private JobToolBar toolbar;

	public JobConfigurationPanel(HandlerManager eventbus) {
		super();
		this.eventbus = eventbus;
		this.setHeaderVisible(false);
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
	}

	public void assembleView() {
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 150,
				100, 250);
		data.setMargins(new Margins(0, 5, 0, 0));
		data.setSplit(true);
		data.setCollapsible(true);
		data.setFloatable(true);
		icj = new IndepdentContrastJobView(eventbus);
		this.add(buildNavigation(), data);
		buildBottomComponent();
		this.setBottomComponent(toolbar);
		
		eventbus.addHandler(DataSelectedEvent.TYPE, new DataSelectedEventHandler() {
			
			@Override
			public void onDataSelected(DataSelectedEvent dse) {
					if(dse.isSelected() && dse.getStep()!= store.getCount()) {
						toolbar.getNext().enable();
					} else {
						toolbar.getNext().disable();
					}
					
					if(dse.getStep() != 1 ) {
						toolbar.getPrev().enable();
					} else {
						toolbar.getPrev().disable();
					}
			}
		});

		BorderLayoutData wizardData = new BorderLayoutData(LayoutRegion.CENTER,
				150, 100, 250);
		wizardData.setMargins(new Margins(0, 5, 0, 0));
		wizardData.setSplit(true);
		wizardData.setCollapsible(true);
		wizardData.setFloatable(true);
		this.add(icj.getWizard(), wizardData);

		popup = new Dialog();
		popup.getButtonBar().hide();
		popup.setLayout(new FitLayout());
		popup.setHeight(400);
		popup.setWidth(600);
		popup.setHeading("Configure New Job");
		popup.add(this);
		popup.setModal(true);
		popup.show();
	}

	/**
	 * Build the navigation panel. This allows users to navigate between
	 * different job configuration steps.
	 * 
	 * @return
	 */
	private ContentPanel buildNavigation() {
		navpanel = new ContentPanel();
		navpanel.setHeaderVisible(true);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setWidth(150);
		column.setMenuDisabled(true);
		configs.add(column);

		store = new ListStore<JobStep>();
		ColumnModel cm = new ColumnModel(configs);

		grid = new Grid<JobStep>(store, cm);
		grid.setAutoHeight(true);
		grid.setBorders(false);
		grid.setHideHeaders(true);

		grid.setAutoExpandColumn("name");
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setAutoWidth(true);
		store.add(icj.getJobConfigSteps());
		grid.disableEvents(true);
		grid.addListener(Events.RowClick, new Listener<BaseEvent>() {
			@SuppressWarnings("unchecked")
			public void handleEvent(BaseEvent be) {
			}
		});

		BufferView view = new BufferView();
		view.setRowHeight(30);
		updateStep(1);
		grid.setView(view);
		grid.getSelectionModel().select(0, false);
		navpanel.add(grid);

		return navpanel;
	}

	private void updateStep(int rowIndex) {
		navpanel.setHeading("Step(s) " + rowIndex + " of " + store.getCount());
	}

	private void hidePanel() {
		popup.hide();
	}

	/**
	 * Build bottom component toolbar with prev, next buttons
	 * 
	 * @return
	 */
	private void buildBottomComponent() {
		toolbar = new JobToolBar();
		
		toolbar.getNext().disable();
		toolbar.getPrev().disable();
		toolbar.getSave().disable();
		
		toolbar.getNext().addListener(Events.OnClick,
				new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						JobStep step = grid.getSelectionModel()
								.getSelectedItem();
						grid.getSelectionModel().select(
								Integer.parseInt(step.get("step").toString()),
								false);
						updateStep(Integer
								.parseInt(step.get("step").toString()) + 1);
						
						JobToolBarNextClickEvent event = new JobToolBarNextClickEvent(step);
						eventbus.fireEvent(event);
						toolbar.getPrev().enable();
						if (Integer.parseInt(step.get("step").toString()) + 1 == store
								 .getCount()) {
							 toolbar.getNext().disable();
							 toolbar.getSave().enable();
						}
					}
				});
		
		toolbar.getPrev().addListener(Events.OnClick,
				new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						JobStep step = grid.getSelectionModel()
								.getSelectedItem();
						grid.getSelectionModel()
								.select(
										Integer.parseInt(step.get("step")
												.toString()) - 2, false);
						updateStep(Integer
								.parseInt(step.get("step").toString()) - 1);
						step = grid.getSelectionModel().getSelectedItem();
						JobToolBarPrevClickEvent event = new JobToolBarPrevClickEvent(step);
						eventbus.fireEvent(event);
						toolbar.getSave().disable();
						if (Integer.parseInt(step.get("step").toString())  == store
								 .getCount() - 1) {
							 toolbar.getNext().enable();
						}
					}
				});
				
		toolbar.getSave().addListener(Events.OnClick, new Listener<BaseEvent>() 
		{
			@Override
			public void handleEvent(BaseEvent be) 
			{
				final MessageBox box = MessageBox.prompt("Job Name", "Please enter a new name for your job:");  
				        box.addCallback(new Listener<MessageBoxEvent>() 
				{  
			          public void handleEvent(MessageBoxEvent be) 
			          {  
			        	  //TODO: call service to add job
			        	  /*
			        	  Job job = new Job("",be.getValue(),"Not Started");
			            
			        	  RPCFacade.addJob(job,new AsyncCallback<List<Job>>() 
			        	  {			
			        		  @Override
			        		  public void onSuccess(List<Job> result) 
			        		  {
			        			  eventbus.fireEvent(new JobStatusChangeEvent(result));
			        		  }
							
			        		  @Override
			        		  public void onFailure(Throwable caught) 
			        		  {
			        			  // TODO: handle failure				
			        		  }
			        	  }); */	
						
			        	  popup.hide();
			          }  
				});  				
			}
			
		});

		toolbar.getCancel().addListener(Events.OnClick,
				new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						hidePanel();
					}
				});
	}

}
