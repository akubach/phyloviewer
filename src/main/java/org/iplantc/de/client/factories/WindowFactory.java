package org.iplantc.de.client.factories;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.windows.DataBrowserWindow;
import org.iplantc.de.client.windows.IplantWindow;
import org.iplantc.de.client.windows.JobsManagementWindow;

import com.google.gwt.core.client.GWT;

public class WindowFactory 
{
	//////////////////////////////////////////
	//private variables
	private static WindowFactory instance;	
	private static DEClientConstants constants = (DEClientConstants)GWT.create(DEClientConstants.class);
	
	//////////////////////////////////////////
	//constructor
	private WindowFactory()
	{		
	}
	
	//////////////////////////////////////////
	//public methods
	public static WindowFactory getInstance() 
	{
		if(instance == null) 
		{
			instance = new WindowFactory();
		}
		
		return instance;
	}	

	//////////////////////////////////////////
	public IplantWindow buildWindow(String idWorkspace,String tag)
	{
		IplantWindow ret = null;
	
		if(tag.equals(constants.myDataTag()))
		{
			ret = new DataBrowserWindow(tag,idWorkspace); 
		}
		else if(tag.equals(constants.myJobsTag()))
		{
			ret = new JobsManagementWindow(tag,idWorkspace);
		}
		
		return ret;
	}	
}
