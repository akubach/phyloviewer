package org.iplantc.de.server;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.iplantc.de.client.services.ServiceCallWrapper;

public class TestDEServiceDispatcher extends TestCase implements ServletContext
{	
	public void testNoAddress() throws Exception
	{
		DEServiceDispatcher dispatcher = new DEServiceDispatcher();
		dispatcher.setContext(this);
		ServiceCallWrapper wrapper = new ServiceCallWrapper();
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPutBodyNull() throws Exception
	{
		DEServiceDispatcher dispatcher = new DEServiceDispatcher();
		dispatcher.setContext(this);
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"some.address",null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPutBodyEmpty() throws Exception
	{
		DEServiceDispatcher dispatcher = new DEServiceDispatcher();
		dispatcher.setContext(this);
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"some.address","");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPostBodyNull() throws Exception
	{
		DEServiceDispatcher dispatcher = new DEServiceDispatcher();
		dispatcher.setContext(this);
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"some.address",null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);	
	}
	
	public void testPostBodyEmpty() throws Exception
	{
		DEServiceDispatcher dispatcher = new DEServiceDispatcher();
		dispatcher.setContext(this);
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"some.address","");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testDeleteAddressNull() throws Exception
	{
		DEServiceDispatcher dispatcher = new DEServiceDispatcher();
		dispatcher.setContext(this);
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);	
	}
	
	public void testDeleteAddressEmpty() throws Exception
	{
		DEServiceDispatcher dispatcher = new DEServiceDispatcher();
		dispatcher.setContext(this);
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,"");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}

	@Override
	public Object getAttribute(String arg0)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getAttributeNames()
	{
		return null;
	}

	@Override
	public ServletContext getContext(String arg0)
	{
		return this;
	}

	@Override
	public String getContextPath()
	{
		return null;
	}

	@Override
	public String getInitParameter(String arg0)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getInitParameterNames()
	{
		return null;
	}

	@Override
	public int getMajorVersion()
	{
		return 0;
	}

	@Override
	public String getMimeType(String arg0)
	{
		return null;
	}

	@Override
	public int getMinorVersion()
	{
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0)
	{
		return null;
	}

	@Override
	public String getRealPath(String arg0)
	{
		return "war";
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0)
	{
		return null;
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException
	{
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set getResourcePaths(String arg0)
	{
		return null;
	}

	@Override
	public String getServerInfo()
	{
		return null;
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException
	{
		return null;
	}

	@Override
	public String getServletContextName()
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getServletNames()
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getServlets()
	{
		return null;
	}

	@Override
	public void log(String arg0)
	{
	}

	@Override
	public void log(Exception arg0, String arg1)
	{
	}

	@Override
	public void log(String arg0, Throwable arg1)
	{
	}

	@Override
	public void removeAttribute(String arg0)
	{
	}

	@Override
	public void setAttribute(String arg0, Object arg1)
	{
	}
	
}