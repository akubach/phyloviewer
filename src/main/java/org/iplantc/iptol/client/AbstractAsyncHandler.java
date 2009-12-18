package org.iplantc.iptol.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author sriram
 * @param <T>
 *            An abstract class that implements AsyncCallback and AsyncHandler.
 *            Classes extending this should implment the abstract methods.
 *            Provide an abstract callback superclass that can be subclassed to
 *            add specific behaviors (such as logging). Give the superclass the
 *            ability to handle the RPC result before the subclass.
 */
public abstract class AbstractAsyncHandler implements AsyncCallback,
		AsyncHandler {

	public void onFailure(Throwable caught) {
		handleFailure(caught);
	}

	public void onSuccess(Object result) {
		handleSuccess(result);
	}

	public abstract void handleFailure(Throwable caught);

	public abstract void handleSuccess(Object result);

}
