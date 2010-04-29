package org.iplantc.de.client.models;

public class FileIdentifier 
{
	///////////////////////////////////////
	//private variables
	private String idParent;
	private String idFile;
	private String filename;
	
	///////////////////////////////////////
	//constructor
	public FileIdentifier(String filename,String idParent,String idFile)
	{
		this.filename = filename;
		this.idParent = idParent;
		this.idFile = idFile;
	}
	
	///////////////////////////////////////
	//public methods
	public String getParentId()
	{
		return idParent;
	}
	
	///////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}

	///////////////////////////////////////
	public String getFilename()
	{
		return filename;
	}	
}
