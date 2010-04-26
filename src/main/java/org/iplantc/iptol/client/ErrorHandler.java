package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class ErrorHandler 
{
	private static IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);

	public static void post(String error)
	{
		// we're looking at making it so all error msg-boxes have red icons and not the yellow/yield icons
		//MessageBox.alert(errorStrings.error(), error, null);
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitle(errorStrings.error());
		msgError.setMessage(error);
		msgError.show();
	}	
}
