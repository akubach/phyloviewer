package org.iplantc.de.client.views.tabs;

import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.views.panels.ProvenanceContentPanel;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

/**
 * Provides a user interface definition for presenting provenanace. 
 */
public class ProvenanceTab extends TabItem
{
	// /////////////////////////////////////
	// private variables
	private ProvenanceContentPanel panel;

	// /////////////////////////////////////
	// constructor
	public ProvenanceTab(ProvenanceContentPanel panel, String provenance)
	{
		super();
		this.panel = panel;

		setHeight(410);

		setHeader();
		updateProvenance(provenance);
		setLayout(new FitLayout());
	}

	// /////////////////////////////////////
	// private methods
	private void setHeader()
	{
		if(panel != null)
		{
			String header = panel.getTabHeader();

			if(header != null)
			{
				setText(header);
			}
		}
	}

	// /////////////////////////////////////
	// protected methods
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);

		if(panel != null)
		{
			add(panel);
		}
	}

	// /////////////////////////////////////
	// public methods
	public void updateProvenance(String provenance)
	{
		if(panel != null)
		{
			panel.updateProvenance(provenance);
		}
	}

	// /////////////////////////////////////
	public void setFileIdentifier(FileIdentifier file)
	{
		if(panel != null)
		{
			panel.setFileIdentifier(file);
		}
	}

	// /////////////////////////////////////
	public boolean isDirty()
	{
		return (panel == null) ? false : panel.isDirty();
	}
}
