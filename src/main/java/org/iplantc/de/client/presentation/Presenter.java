package org.iplantc.de.client.presentation;

import org.iplantc.de.client.views.View;

import com.google.gwt.user.client.ui.RootPanel;

public abstract class Presenter
{
	//////////////////////////////////////////
	//protected variables
	protected View view;
	
	//////////////////////////////////////////
	//constructor
	protected Presenter(View view)
	{
		this.view = view;	
	}
	
	//////////////////////////////////////////
	//protected methods	
	protected abstract void updateView(String cmd,String params);
		
	//////////////////////////////////////////
	//public methods
	public void display(String cmd,String params) 
	{
		updateView(cmd,params);		
	}

	//////////////////////////////////////////
	public void destroy() 
	{
		if(view != null)
		{
			RootPanel.get().remove(view.getDisplayWidget());
		}		
	}	
}
