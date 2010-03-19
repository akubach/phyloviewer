package org.iplantc.iptol.client.dialogs;

import org.iplantc.iptol.client.dialogs.panels.IPlantDialogPanel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class IPlantDialog extends Dialog 
{
	//////////////////////////////////////////
	//private variables
	private IPlantDialogPanel panel;
	
	//////////////////////////////////////////
	//constructor
	public IPlantDialog(String caption,int width,IPlantDialogPanel panel)	
	{
		this.panel = panel;
		
		if(panel != null)
		{
			panel.setButtonBar(getButtonBar());
		}
		
		setButtons(Dialog.OKCANCEL);
		setButtonAlign(HorizontalAlignment.RIGHT);		
		setLayout(new FitLayout());
		setResizable(false);
		setModal(true);
		setHideOnButtonClick(true);
		setHeading(caption);
		setWidth(width);
		
		setupEventHandlers();
	}
	
	//////////////////////////////////////////
	//private methods
	private void setupEventHandlers()
	{		
		//handle ok button
	    getButtonById("ok").addSelectionListener(new SelectionListener<ButtonEvent>() 
	    {
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				if(panel != null)
				{
					panel.handleOkClick();
				}								
			}			
		});
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		if(panel != null)
		{
			Widget w = panel.getDisplayWidget();
		
			add(w);
			
			setFocusWidget(w);
		}
	}
}
