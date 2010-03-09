package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Job extends BaseModel implements IsSerializable 
{
	private static final long serialVersionUID = 1289771632882434904L;

	/////////////////////////////////////////////
	//constructors
	public Job()
	{
		set("id","");	
		set("name","");
		set("status","");
	}
	
	/////////////////////////////////////////////
	//constructors
	public Job(String id,String name,String status)
	{
		set("id",id);	
		set("name",name);
		set("status",status);
	}	
}
