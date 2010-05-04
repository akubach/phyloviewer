package org.iplantc.de.client.factories;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.views.windows.DataBrowserWindow;
import org.iplantc.de.client.views.windows.IPlantWindow;
import org.iplantc.de.client.views.windows.JobsManagementWindow;

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
	public IPlantWindow buildWindow(String idWorkspace,String tag)
	{
		IPlantWindow ret = null;
	
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
