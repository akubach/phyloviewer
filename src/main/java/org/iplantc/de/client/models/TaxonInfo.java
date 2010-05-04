package org.iplantc.de.client.models;

import com.google.gwt.core.client.JavaScriptObject;

public class TaxonInfo extends JavaScriptObject 
{
	protected TaxonInfo()
	{		
	}
	
	//JSNI methods to get taxon info	
	public final native int getClusterId() /*-{ return this.clusterId; }-*/;
	public final native int getTaxonId() /*-{return this.taxonId; }-*/;
	public final native String getTaxonName() /*-{ return this.taxonName; }-*/;
}
