package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class ErrorHandler 
{
	private static IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);

	public static void post(String error)
	{
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitle(errorStrings.error());
		msgError.setMessage(error);
		msgError.show();
	}	
}
