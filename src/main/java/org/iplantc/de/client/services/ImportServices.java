package org.iplantc.de.client.services;

import org.iplantc.de.client.DEServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportServices 
{
	/**
	 * Call service to retrieve the search results from the phylota database
	 * @param nameTaxon
	 * @param callback
	 */
	public static void getSearchResults(String nameTaxon,AsyncCallback<String> callback)
	{		
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://ceiba.biosci.arizona.edu:14444/clusters?taxon_name=" + nameTaxon);
		DEServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to retrieve a tree from the database
	 * @param nameTaxon
	 * @param callback
	 */
	public static void getTree(String idTaxon,String idCluster,AsyncCallback<String> callback)
	{		
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://ceiba.biosci.arizona.edu:14444/trees/" + idTaxon + "/" + idCluster);
		DEServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
}
