package org.iplantc.de.client;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

/**
 * Provides a uniform manner for posting errors to the user. 
 */
public class ErrorHandler
{
	private static DEErrorStrings errorStrings = (DEErrorStrings)GWT.create(DEErrorStrings.class);

	/**
	 * Post a message box with error styles with the argument error message. 
	 * @param error the string message to include in the notification
	 */
	public static void post(String error)
	{
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitle(errorStrings.error());
		msgError.setMessage(error);
		msgError.show();
	}
}
