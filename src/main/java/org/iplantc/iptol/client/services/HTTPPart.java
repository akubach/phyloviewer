package org.iplantc.iptol.client.services;

import java.io.Serializable;

public class HTTPPart implements Serializable
{
	private static final long serialVersionUID = -2662589032061446564L;
	private String body = new String();
	private String disposition = new String();
	
	public HTTPPart()
	{
		
	}
	
	public HTTPPart(String body,String disposition)
	{
		this.body = body;
		this.disposition = disposition;
	}
	
	public String getBody()
	{
		return body;
	}
	
	public String getDisposition()
	{
		return disposition;
	}
}
