package org.iplantc.de.client;

import com.google.gwt.i18n.client.Constants;

/**
 * Constants used by client code not visible to the user.  
 */
public interface DEClientConstants extends Constants
{
	int statusInterval();

	String fileUploadServlet();

	/**
	 * The path to the iPlant logo resource.
	 * @return a string representation of the path to iPlant logo
	 */
	String iplantLogo();

	/**
	 * The tag used by the window manager to identify the My Data window.
	 * @return a string representing the handle for the My Data window
	 */
	String myDataTag();

	/**
	 * The tag used by the window manager to identify the File Editor window. 
	 * @return a string representing the handle for the File Editor window 
	 */
	String fileEditorTag();

	/**
	 * The default tag prefix used by the window manager when creating a "handle" for a window.
	 * @return a string representing the default prefix used for a window
	 */
	String windowTag();

	/**
	 * The default tag prefix used to identify "actions", operations performed by the window 
	 * manager that are not necessarily visible to the user.
	 * @return a string representing the default prefix for an action
	 */
	String actionTag();

	String action();

	String tag();

	/**
	 * The history token for the logout operation.
	 * @return a string representing the history token for logout
	 */
	String logoutTag();

	/**
	 * The tag used by the window manager to identify the My Jobs window.
	 * @return a string representing the handle for My Jobs window
	 */
	String myJobsTag();
}
