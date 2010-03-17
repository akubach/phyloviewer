package org.iplantc.iptol.client.models;

public class RawData 
{
	//////////////////////////////////////////
	//private variables
	private String data;
	private String provenance;
	
	//////////////////////////////////////////
	//constructors
	public RawData()
	{		
	}
	
	//////////////////////////////////////////
	public RawData(String header,String data,String provenance)
	{
		this.setData(data);
		this.setProvenance(provenance);
	}

	//////////////////////////////////////////
	//public methods
	public void setData(String data) 
	{
		this.data = data;
	}

	//////////////////////////////////////////
	public String getData() 
	{
		return data;
	}

	//////////////////////////////////////////
	public void setProvenance(String provenance) 
	{
		this.provenance = provenance;
	}

	//////////////////////////////////////////
	public String getProvenance() 
	{
		return provenance;
	}	
}
