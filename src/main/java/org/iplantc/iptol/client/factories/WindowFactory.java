package org.iplantc.iptol.client.factories;

import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.windows.DataBrowserWindow;
import org.iplantc.iptol.client.windows.IplantWindow;
import org.iplantc.iptol.client.windows.JobsManagementWindow;

import com.google.gwt.core.client.GWT;

public class WindowFactory 
{
	//////////////////////////////////////////
	//private variables
	private static WindowFactory instance;	
	private static IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	
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
