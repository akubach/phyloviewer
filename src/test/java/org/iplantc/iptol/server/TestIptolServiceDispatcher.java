package org.iplantc.iptol.server;

import junit.framework.TestCase;

import org.iplantc.iptol.client.services.ServiceCallWrapper;

public class TestIptolServiceDispatcher extends TestCase
{
	public void testNullServiceCallWrapper()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		
		String test = dispatcher.getServiceData(null);
		assertNull(test);		
	}
	
	public void testNoAddress()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper();
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPutBodyNull()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"some.address",null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPutBodyEmpty()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"some.address","");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testPostBodyNull()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"some.address",null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);	
	}
	
	public void testPostBodyEmpty()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"some.address","");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
	
	public void testDeleteAddressNull()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,null);
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);	
	}
	
	public void testDeleteAddressEmpty()
	{
		IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,"");
		
		String test = dispatcher.getServiceData(wrapper);
		assertNull(test);		
	}
}
