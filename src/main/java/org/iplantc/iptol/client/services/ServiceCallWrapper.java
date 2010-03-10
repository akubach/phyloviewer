package org.iplantc.iptol.client.services;

import java.io.Serializable;

public class ServiceCallWrapper implements Serializable 
{	
	private static final long serialVersionUID = -7453647589756124397L;
	private String address = new String();
	private String body = new String();
	private String disposition = new String();
	
	private Type type = Type.GET;
	
	public enum Type
	{
		GET,
		PUT,
		POST,
		POST_MULTIPART,
		DELETE
	}
	
	public ServiceCallWrapper()
	{
	}
	
	public ServiceCallWrapper(String address)
	{
		this.address = address; 
	}

	public ServiceCallWrapper(Type type,String address)
	{
		this(address);
		this.type = type; 
	}
	
	public ServiceCallWrapper(Type type,String address,String body)
	{
		this(type,address);		
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
	
	public void setDisposition(String disposition)
	{
		this.disposition = disposition;
	}
	
	public String getDisposition()
	{
		return disposition;
	}
}
