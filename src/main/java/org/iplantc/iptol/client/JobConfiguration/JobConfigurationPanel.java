package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.ToggleButton;

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
	private ArrayList<ToggleButton> stepBtns;
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
		//this.add(buildNavigation(), data);
		buildNavigationPanel();
		this.add(navpanel,data);
		buildBottomComponent();
		this.setBottomComponent(toolbar);
		
		eventbus.addHandler(DataSelectedEvent.TYPE, new DataSelectedEventHandler() {
			
			@Override
			public void onDataSelected(DataSelectedEvent dse) {
					if(dse.isSelected() && dse.getStep() + 1 != stepBtns.size()) {
						toolbar.getNext().enable();
						stepBtns.get(dse.getStep() + 1).setEnabled(true);
					} else {
						toolbar.getNext().disable();
						stepBtns.get(dse.getStep() + 1).setEnabled(false);
					}
					
					//first step
					if(dse.getStep() != 0 ) {
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
	private void buildNavigationPanel() {
		navpanel = new ContentPanel();
		navpanel.setFrame(true);
		navpanel.setHeaderVisible(true);
		ArrayList<JobStep> steps = icj.getJobConfigSteps();
		stepBtns = new ArrayList<ToggleButton>();
		ToggleButton stepBtn = null;
		for(JobStep step : steps) {
			stepBtn = new ToggleButton(step.getName());
			stepBtn.setEnabled(false);
			stepBtn.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					handleToogleClick(event.getSource());
				}
			
			});
			navpanel.add(stepBtn);
			stepBtns.add(stepBtn);
		}
		//first step down by default
		stepBtns.get(0).setEnabled(true);
		stepBtns.get(0).setDown(true);
		
	}
	
	private void handleToogleClick(Object source) {
			ToggleButton button = (ToggleButton)source;
			ToggleButton btn = null;
			JobStep step = null;
			for(int i=0;i<stepBtns.size();i++) {
				btn = stepBtns.get(i);
				if(btn.getText().equals(button.getText())) {
					btn.setDown(true);
					updateStep(i);
					step = new JobStep(i,stepBtns.get(i).getText());
					NavButtonClickEvent event = new NavButtonClickEvent(step);
					eventbus.fireEvent(event);
					//first step
					if(i == 0) {
						toolbar.getPrev().disable();
						
					} //last step
					 else if (i + 1 == stepBtns.size()) {
						toolbar.getNext().disable();
					}
				} else {
					btn.setDown(false);
				}
			}
		
	}

	
	private void updateStep(int rowIndex) {
		navpanel.setHeading("Step(s) " + (rowIndex + 1) + " of " + stepBtns.size());
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
						ToggleButton btn = null;
						JobStep step = null;
						int step_no = 0;
						//job starts with step 0
						//find current step
						for(int i=0;i<stepBtns.size();i++) {
							btn = stepBtns.get(i);
							if(btn.isDown() == true) {
								step = new JobStep(i,btn.getText());
								step_no = i;
								break;
							}
						}
						
						//go to next step if there is one
						updateStep(step_no + 1);
						if(step_no < stepBtns.size()) {
							stepBtns.get(step_no + 1).setDown(true);
							stepBtns.get(step_no + 1).setEnabled(true);
							handleToogleClick(stepBtns.get(step_no + 1));
						}
						
						JobToolBarNextClickEvent event = new JobToolBarNextClickEvent(step);
						eventbus.fireEvent(event);
						toolbar.getPrev().enable();

						//enable/ disable next & prev correctly for last step
 						if(step_no + 1 == stepBtns.size()) {
							 toolbar.getNext().disable();
							 toolbar.getSave().enable();
						}
						
					}
				});
		
		toolbar.getPrev().addListener(Events.OnClick,
				new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						ToggleButton btn = null;
						JobStep step = null;
						int step_no = 0;
						//job starts with step 0
						//find current step
						for(int i=0;i<stepBtns.size();i++) {
							btn = stepBtns.get(i);
							if(btn.isDown() == true) {
								step = new JobStep(i,btn.getText());
								step_no = i ;
								break;
							}
						}
						
						//go prev. step
						updateStep(step_no - 1);
						if(step_no - 1 >= 0) {
							stepBtns.get(step_no - 1).setDown(true);
							stepBtns.get(step_no - 1 ).setEnabled(true);
							handleToogleClick(stepBtns.get(step_no - 1));
							JobToolBarPrevClickEvent event = new JobToolBarPrevClickEvent(step);
							eventbus.fireEvent(event);
							toolbar.getSave().disable();
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
