package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.events.LoginEvent;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerManager;

public class LoginPanel extends FormPanel
{	
	//////////////////////////////////////////
	//private variables
	private HandlerManager eventbus;
	private TextField<String> fieldName = new TextField<String>();
	private TextField<String> fieldPass = new TextField<String>();
	
	//////////////////////////////////////////
	//constructor
	public LoginPanel(final HandlerManager eventbus)
	{
		this.eventbus = eventbus;		
		initOuterPanel();
		
		initNameField();
		initPasswordField();
		initLoginButton();				
	}
	
	//////////////////////////////////////////
	//private methods
	private void initOuterPanel()
	{	
		setFrame(true);
		setTitle("Login");
		setWidth(350);
		setLabelWidth(75);
		setStyleAttribute("margin","10px");
		setButtonAlign(HorizontalAlignment.CENTER);
		setHeading("Login");			
	}
	
	//////////////////////////////////////////
	private void initNameField()
	{
		fieldName.setFieldLabel("Username");	
		fieldName.setSelectOnFocus(true);
		fieldName.focus();
		
		add(fieldName); 	
	}
	
	//////////////////////////////////////////
	private void initPasswordField()
	{
		fieldPass.setFieldLabel("Password");
		fieldPass.setPassword(true);
	
		//if the user hits the enter key, treat it the same as if the user clicked the login button 
		fieldPass.addKeyListener(new KeyListener()
		{
			public void componentKeyUp(ComponentEvent event)
			{
				if(event.getKeyCode() == KeyCodes.KEY_ENTER)
				{
					doLogin();
				}
			}
		});
		
		add(fieldPass);		
	}
	
	//////////////////////////////////////////
	private void doLogin()
	{
		LoginEvent event = new LoginEvent(fieldName.getValue(),fieldPass.getValue());
		eventbus.fireEvent(event);			
	}
	
	//////////////////////////////////////////
	private void initLoginButton()
	{
		//login button
		addButton(new Button("Login", new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{			
				doLogin();  
			}
		}));	    	
	}	
}
