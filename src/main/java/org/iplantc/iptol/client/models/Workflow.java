package org.iplantc.iptol.client.models;

import java.util.ArrayList;
import java.util.List;

public class Workflow 
{
	//////////////////////////////////////////
	//protected variables
	protected String name;
	protected List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
	
	
	//////////////////////////////////////////
	//constructors
	public Workflow(String name)
	{
		this.name = name;
	}
	
	//////////////////////////////////////////
	public Workflow(String name,List<WorkflowStep> steps)
	{
		this(name);
		
		if(steps != null)
		{
			this.steps = steps;
		}
	}
	
	//////////////////////////////////////////
	public String getName()
	{
		return name;
	}
	
	//////////////////////////////////////////
	public int getNumSteps()
	{
		return steps.size();
	}
	
	//////////////////////////////////////////
	public void addStep(WorkflowStep step)
	{
		steps.add(step);
	}
	
	//////////////////////////////////////////
	public List<WorkflowStep> getSteps()
	{
		return steps;
	}
	
	//////////////////////////////////////////
	public void clearSteps()
	{
		steps.clear();
	}	
}
