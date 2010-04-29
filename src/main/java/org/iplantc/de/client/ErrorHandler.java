package org.iplantc.de.client;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class ErrorHandler 
{
	private static DEErrorStrings errorStrings = (DEErrorStrings) GWT.create(DEErrorStrings.class);

	public static void post(String error)
	{
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitle(errorStrings.error());
		msgError.setMessage(error);
		msgError.show();
	}	
}
