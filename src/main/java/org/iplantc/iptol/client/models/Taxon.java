package org.iplantc.iptol.client.models;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Taxon extends BaseModel
{
	private static final long serialVersionUID = 3666132083691854752L;

	public Taxon(TaxonInfo info)
	{
		setClusterId(info.getClusterId());
		setTaxonId(info.getTaxonId());
		setTaxonName(info.getTaxonName());
	}
	
	public void setClusterId(int id)
	{
		set("clusterId",Integer.toString(id));
	}
	
	public void setTaxonId(int id)
	{
		set("taxonId",Integer.toString(id));
	}
	
	public void setTaxonName(String name)
	{
		set("taxonName",name);
	}
	
	public String getClusterId()
	{
		return get("clusterId");
	}
	
	public String getTaxonId()
	{
		return get("taxonId");
	}
	
	public String getTaxonName()
	{
		return get("taxonName");
	}
}
