package org.iplantc.iptol.client;

import java.io.Serializable;

public class ServiceCallWrapper implements Serializable 
{	
	private static final long serialVersionUID = -7453647589756124397L;
	private String address = new String();
	private String body = new String();
	private Type type = Type.GET;
	
	public enum Type
	{
		GET,
		PUT,
		POST,
		DELETE
	}
	
	public ServiceCallWrapper()
	{
	}
	
	public ServiceCallWrapper(String address)
	{
		this.address = address; 
	}

	public ServiceCallWrapper(Type type,String address,String body)
	{
		this(address);
		this.type = type;
		this.body = body;
	}

	public Type getType()
	{
		return type;
	}
	
	public String getAddress()
	{
		return address;		
	}
	
	public String getBody()
	{
		return body;
	}
}
