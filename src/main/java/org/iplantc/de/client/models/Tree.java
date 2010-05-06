/**
 * 
 */
package org.iplantc.de.client.models;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Models the metadata related to a phylogenetic Tree. 
 * 
 * @author sriram
 */
public class Tree extends BaseModelData
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Tree(String id, String filename, String treename, String uploaded)
	{
		set("id", id);
		set("filename", filename);
		set("treename", treename);
		set("uploaded", uploaded);
	}

	public String getFilename()
	{
		return get("filename").toString();
	}

	public String getTreename()
	{
		return get("treename").toString();
	}

	public String getUploaded()
	{
		return get("uploded").toString();
	}

	public String getId()
	{
		return get("id").toString();
	}
}
