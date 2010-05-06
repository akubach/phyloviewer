package org.iplantc.de.server;

import javax.servlet.http.HttpSession;

import org.iplantc.de.client.services.SessionManagementService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author sriram A class that manages client session
 * 
 */
public class SessionManagementServlet extends RemoteServiceServlet implements SessionManagementService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object getAttribute(String key) throws SerializationException
	{
		return getSession().getAttribute(key);
	}

	@Override
	public void invalidate() throws SerializationException
	{
		getSession().invalidate();

	}

	@Override
	public void removeAttribute(String key) throws SerializationException
	{
		getSession().removeAttribute(key);

	}

	@Override
	public void setAttribute(String key, Object value) throws SerializationException
	{
		getSession().setAttribute(key, value);
	}

	private HttpSession getSession()
	{
		return this.getThreadLocalRequest().getSession();
	}

}
