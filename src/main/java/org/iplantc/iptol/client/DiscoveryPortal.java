package org.iplantc.iptol.client;

import java.util.HashMap;

import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;

/**
 * 
 * @author sriram
 * A class that extends GXT portal. This is used to track the portlets contained with-in the portal.
 * Provides customization ability
 */
public class DiscoveryPortal extends Portal{

	private HashMap<String,Portlet> portlets;
	
	public DiscoveryPortal(int numColumns) {
		super(numColumns);
		portlets = new HashMap<String,Portlet>();
	}
	
	public void addPortlet(Portlet p, String key) {
		portlets.put(key, p);
	}
	
	public Portlet getPortlet(String key) {
		return portlets.get(key);
	}
	
}
