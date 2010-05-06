package org.iplantc.de.server;

/**
 * The MatrixInfo provides a summary of a Matrix (Trait Table).
 * 
 * @author Donald A. Barre
 */
public class MatrixInfo
{

	private String filename; // the file the matrix was in
	private String uploaded; // when the file was uploaded
	private String id; // the id of the matrix

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setUploaded(String uploaded)
	{
		this.uploaded = uploaded;
	}

	public String getUploaded()
	{
		return uploaded;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}
}
