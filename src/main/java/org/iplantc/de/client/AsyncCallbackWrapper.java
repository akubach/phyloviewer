package org.iplantc.de.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * Detects when the user is not logged in to the application and redirects the user to the login page.  The
 * authentication filter always responds with a 403 status code, but we have also have to check for a status
 * code of zero because that seems to be what StatusCodeException.getStatusCode() always returns.
 *
 * @author Dennis Roberts
 *
 * @param <T> the type of the result we're expecting to get from the server.
 */
public class AsyncCallbackWrapper<T> implements AsyncCallback<T> {

    /**
     * The callback that we're wrapping.
     */
    AsyncCallback<T> callback;

    /**
     * Creates a new callback wrapper.
     *
     * @param callback the callback that we're wrapping.
     */
    public AsyncCallbackWrapper(AsyncCallback<T> callback) {
        this.callback = callback;
    }

    /**
     * Called whenever a call to the server fails.  If the call failed because of an HTTP status code and that status
     * code is either 403 or wasn't recorded then we assume that the user isn't logged in and redirect the user to the
     * login page.  The callback that we're wrapping deals with all other errors.
     * 
     * @param error the exception or error that indicates why the call failed.
     */
    @Override
    public void onFailure(Throwable error) {
        if (error instanceof StatusCodeException) {
            int statusCode = ((StatusCodeException) error).getStatusCode();
            if (statusCode == 403 || statusCode == 0) {
                Window.Location.replace(Window.Location.getPath() + "/jsp/login.jsp");
                return;
            }
        }
        callback.onFailure(error);
    }

    /**
     * Called whenever a call to the server succeeds.  The callback that we're wrapping deals with all successful
     * calls.
     * 
     * @param response the response from the server.
     */
    @Override
    public void onSuccess(T response) {
        callback.onSuccess(response);
    }

}
