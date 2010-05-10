package org.iplantc.de.client.services;

import org.iplantc.phyloviewer.shared.DEServiceFacade;
import org.iplantc.phyloviewer.shared.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for importing data from external sources. 
 */
public class ImportServices
{
	/**
	 * Call service to retrieve the search results from the phylota database
	 * 
	 * @param nameTaxon
	 * @param callback
	 */
	public static void getSearchResults(String nameTaxon, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(
				"http://ceiba.biosci.arizona.edu:14444/clusters?taxon_name=" + nameTaxon);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to retrieve a tree from the database
	 * 
	 * @param nameTaxon
	 * @param callback
	 */
	public static void getTree(String idTaxon, String idCluster, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(
				"http://ceiba.biosci.arizona.edu:14444/trees/" + idTaxon + "/" + idCluster);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}
}
