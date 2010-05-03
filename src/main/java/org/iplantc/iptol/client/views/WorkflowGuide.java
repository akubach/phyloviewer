package org.iplantc.iptol.client.views;

import java.util.ArrayList;
import java.util.List;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.events.UserEvent;
import org.iplantc.iptol.client.models.Workflow;
import org.iplantc.iptol.client.models.WorkflowStep;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;

public class WorkflowGuide extends HorizontalPanel 
{
	//////////////////////////////////////////
	//private methods
	private Workflow workflow;
	private List<Button> buttons = new ArrayList<Button>();
	
	//////////////////////////////////////////
	//constructor
	public WorkflowGuide(Workflow workflow)
	{
		setWorkflow(workflow);
	}
	
	//////////////////////////////////////////
	//private methods
	private void addArrow()
	{
		Button btn = new Button(">");
		buttons.add(btn);
	}
	
	//////////////////////////////////////////
	//protected methods
	private void addButton(int idx,WorkflowStep step)
	{
		if(step != null)
		{
			String caption = idx + ". " + step.getName(); 
			Button btn = new Button(caption);
			
			final String action = step.getAction();
			final String tag = step.getTag();
			
			btn.addListener(Events.Select, new SelectionListener<ButtonEvent>() 
			{
				@Override
				public void componentSelected(ButtonEvent ce) 
				{						
					//fire our event
					EventBus eventbus = EventBus.getInstance();
					UserEvent event = new UserEvent(action,tag);
					eventbus.fireEvent(event);
				}
			});	    	
			
			buttons.add(btn);
		}
	}
	
	//////////////////////////////////////////
	private void updateButtons()
	{
		removeAll();
		buttons.clear();
		
		if(workflow != null)
		{
			boolean firstStep = true;
			List<WorkflowStep> steps = workflow.getSteps();
			int idx = 1;
			
			for(WorkflowStep step : steps)
			{
				//we do not add an arrow before our first step
				if(firstStep)
				{
					firstStep = false;
				}
				else
				{
					addArrow();
				}
				
				addButton(idx++, step);
			}		
		}		
	}
	
	//////////////////////////////////////////
	//public methods
	public void setWorkflow(Workflow workflow)
	{
		this.workflow = workflow;
		
		updateButtons();
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		for(Button btn : buttons)
		{
			add(btn);
		}
		
		layout();
	}	
}
