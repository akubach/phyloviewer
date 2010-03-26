package org.iplantc.iptol.client;

import java.util.List;

public class JsonBuilder 
{
	//////////////////////////////////////////
	//private methods
	private static String addQuotes(String in)
	{
		String ret = "'";
		
		if(in != null)
		{
			ret += in;
		}
		
		ret += "'";
		
		return ret;
	}
	
	//////////////////////////////////////////
	private static String addIdToJson(String tag,String id)
	{
		String ret = new String();

		if(tag != null && id != null)
		{
			ret = addQuotes(tag) + ":[" + addQuotes(id) +  ']';
		}
		
		return ret;
	}
	
	//////////////////////////////////////////
	private static String addIdsToJson(String tag,List<String> ids)
	{
		String ret = new String();  //by default we return an empty string
		
		if(tag != null && ids != null)
		{
			if(ids.size() > 0)
			{
				ret = addQuotes(tag) + ":[";
				
				for(String item : ids)
				{
					ret += addQuotes(item);
					ret += ", ";
				}
				
				//trim trailing comma and space
				ret = ret.substring(0,ret.length() - 2);
				ret +=  ']';
			}			
		}
		
		return ret;
	}

	//////////////////////////////////////////
	public static String buildDeleteString(List<String> idFolders,List<String> idFiles)
	{
		String ret = null;
		
		if(idFolders != null && idFiles != null)
		{
			if(idFolders.size() > 0 || idFiles.size() > 0)
			{
				ret = "{";
				
				String files = addIdsToJson("fileIds",idFiles); 
				
				//do we need to append a comma?
				if(files.length() > 0 && idFolders.size() > 0)
				{
					ret += files;
					ret += ", ";
				}
				
				ret += addIdsToJson("folderIds",idFolders);
				ret += "}";					
			}
		}
		
		return ret;
	}
	
	//////////////////////////////////////////
	public static String buildDeleteFolderString(String id)
	{
		String ret = null;
		
		if(id != null)
		{
			ret = "{" + addIdToJson("folderIds",id) + "}";					
		}	
		
		return ret;
	}
	
	//////////////////////////////////////////
	public static String buildDeleteFileString(String id)
	{
		String ret = null;
		
		if(id != null)
		{
			ret = "{" + addIdToJson("fileIds",id) + "}";					
		}	
		
		return ret;
	}	
}
