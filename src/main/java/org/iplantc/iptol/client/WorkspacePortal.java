package org.iplantc.iptol.client;

import java.util.HashMap;

import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;

/**
 * 
 * @author sriram A class that extends GXT portal. This is used to track the
 *         portlets contained with-in the portal. Provides customization ability
 */
public class WorkspacePortal extends Portal {

	private HashMap<String, Portlet> portlets;
	public static final int DEFAULT_NO_COLUMNS = 1;

	public WorkspacePortal(int numColumns) {
		super(numColumns);
		portlets = new HashMap<String, Portlet>();
	}
	
	public WorkspacePortal() {
		super(DEFAULT_NO_COLUMNS);
		portlets = new HashMap<String, Portlet>();
	}

	public void addPortlet(Portlet p, String key) {
		portlets.put(key, p);
	}

	public Portlet getPortlet(String key) {
		return portlets.get(key);
	}

	public void removePorltet(String key) {
		portlets.remove(key);
	}

}
