package org.iplantc.iptol.client.dialogs.panels;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Widget;

public abstract class IPlantPromptPanel extends IPlantDialogPanel 
{
	//////////////////////////////////////////
	//protected variables
	protected TextField<String> field;
	
	//////////////////////////////////////////
	//constructor
	protected IPlantPromptPanel(String caption,int maxLength) 
	{		
		initField(caption,maxLength);
	}

	//////////////////////////////////////////
	//private methods
	private void initField(String caption,int maxLength)
	{
		field = new TextField<String>();
		field.setMaxLength(maxLength);
		
		//if the user hits the enter key, treat it the same as if the user clicked the login button 
		field.addKeyListener(new KeyListener()
		{
			public void componentKeyUp(ComponentEvent event)
			{
				if(event.getKeyCode() == KeyCodes.KEY_ENTER)
				{
					if(parentButtons != null)
					{
						//treat the enter key as if the ok button was clicked
						Component btn = parentButtons.getItemByItemId("ok");					 
						btn.fireEvent(Events.Select);
					}
				}
			}
		});
		
		field.setFieldLabel(caption);		
		field.setWidth(60);
		field.setSelectOnFocus(true);	
		field.focus();
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public Widget getDisplayWidget()
	{
		FormPanel panel = new FormPanel();
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		panel.setPadding(5);
		panel.add(field);
			
		return panel;
	}

	//////////////////////////////////////////
	@Override
	public abstract void handleOkClick();
}
