package org.iplantc.de.client.services;

public class ServiceCallWrapper extends BaseServiceCallWrapper 
{	
	private static final long serialVersionUID = 8930304388034394781L;
	private String body = new String();
		
	public ServiceCallWrapper()
	{
		super();
	}
	
	public ServiceCallWrapper(String address)
	{
		super(address); 
	}

	public ServiceCallWrapper(Type type,String address)
	{
		super(type,address); 
	}
	
	public ServiceCallWrapper(Type type,String address,String body)
	{
		this(type,address);		
		this.body = body;
	}
	
	public String getBody()
	{
		return body;
	}
}
