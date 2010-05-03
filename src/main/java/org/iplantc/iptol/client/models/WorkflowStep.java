package org.iplantc.iptol.client.models;

public class WorkflowStep 
{
	//////////////////////////////////////////
	//protected variables
	protected String name;
	protected String action;
	protected String tag;
		
	//////////////////////////////////////////
	//constructor
	public WorkflowStep(String name,String action,String tag)
	{
		this.name = name;
		this.action = action;
		this.tag = tag;
	}
		
	//////////////////////////////////////////
	//public methods
	public String getName()
	{
		return name;
	}
	
	//////////////////////////////////////////
	public String getAction()
	{
		return action;
	}
	
	//////////////////////////////////////////
	public String getTag()
	{
		return tag;
	}	
}
