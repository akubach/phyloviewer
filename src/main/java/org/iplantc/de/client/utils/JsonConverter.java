package org.iplantc.de.client.utils;

import java.util.List;

import org.iplantc.de.client.models.JsFile;

import com.google.gwt.core.client.JsArray;

public class JsonConverter 
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
				
				ret += addIdsToJson("fileIds",idFiles); 
				
				//do we need to append a comma?
				if(idFiles.size()  > 0 && idFolders.size() > 0)
				{					
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
		
		if(id != null  && id.length() > 0)
		{
			ret = "{" + addIdToJson("folderIds",id) + "}";					
		}	
		
		return ret;
	}
	
	//////////////////////////////////////////
	public static String buildDeleteFileString(String id)
	{
		String ret = null;
		
		if(id != null && id.length() > 0)
		{
			ret = "{" + addIdToJson("fileIds",id) + "}";					
		}	
		
		return ret;
	}	
	
	public static final native JsArray<JsFile> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;	
}
