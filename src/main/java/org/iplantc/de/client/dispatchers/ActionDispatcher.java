package org.iplantc.de.client.dispatchers;

/**
 * Defines an interface for action dispatch operations.
 * 
 * An action is a operation that the window manager executes that is "behind the scenes"
 * and necessarily visible to the user
 */
public interface ActionDispatcher
{
	/**
	 * Dispatch actions associated with tag.
	 * @param tag a handle used by the window manager to identify actions and windows
	 */
	void dispatchAction(String tag);
}
