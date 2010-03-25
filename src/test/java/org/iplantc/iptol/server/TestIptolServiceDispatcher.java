package org.iplantc.iptol.server;

import junit.framework.TestCase;

import org.iplantc.iptol.client.services.ServiceCallWrapper;

public class TestIptolServiceDispatcher extends TestCase
{
	public void testNullServiceCallWrapper() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		
		String test = dispatcher.getServiceData(null);
		assertNull(test);		
	}
	
	public void testNoAddress() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper();
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPutBodyNull() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"some.address",null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPutBodyEmpty() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"some.address","");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPostBodyNull() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"some.address",null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);	
	}
	
	public void testPostBodyEmpty() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"some.address","");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testDeleteAddressNull() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);	
	}
	
	public void testDeleteAddressEmpty() throws Exception
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,"");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
}
