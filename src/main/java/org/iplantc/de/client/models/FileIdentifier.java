package org.iplantc.de.client.models;

/**
 * Models the metadata for a file identifier. 
 * 
 * A file identifier names three parts of information: 
 * <ul>
 * 	<li>parent-id: the identifier of the disk resource parent</li>
 * 	<li>file-id: the identifier of the file resource</li>
 * 	<li>filename: the name of the file resource</li>
 * </ul>
 */
public class FileIdentifier
{
	// /////////////////////////////////////
	// private variables
	private String idParent;
	private String idFile;
	private String filename;

	// /////////////////////////////////////
	// constructor
	public FileIdentifier(String filename, String idParent, String idFile)
	{
		this.filename = filename;
		this.idParent = idParent;
		this.idFile = idFile;
	}

	// /////////////////////////////////////
	// public methods
	public String getParentId()
	{
		return idParent;
	}

	// /////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}

	// /////////////////////////////////////
	public String getFilename()
	{
		return filename;
	}
}
