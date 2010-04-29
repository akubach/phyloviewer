package org.iplantc.de.client;

/**
 * 
 * @author sriram
 * A class that mirrors AsyncCallback interface
 */
public interface AsyncHandler 
{
	void handleSuccess(Object result);
	
	void handleFailure(Throwable caught);
}
