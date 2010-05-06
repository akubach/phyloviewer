package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.events.disk.mgmt.DiskResourceRenamedEvent;
import org.iplantc.de.client.services.DiskResourceRenameCallback;
import org.iplantc.de.client.services.FolderServices;

/**
 * Provides a user interface to prompt the user for renaming a file. 
 */
public class RenameFileDialogPanel extends IPlantPromptPanel
{
	// ////////////////////////////////////////
	// private variables
	private String id;
	private String nameOrig;

	// ////////////////////////////////////////
	// constructor
	public RenameFileDialogPanel(String id, String nameOrig)
	{
		super(displayStrings.fileName(), 250);

		this.nameOrig = nameOrig;
		this.id = id;

		field.setValue(nameOrig);
	}

	// ////////////////////////////////////////
	// private methods
	private void doRename()
	{
		String name = field.getValue();

		if(name != null)
		{
			name = name.trim();

			if(name.length() > 0)
			{
				if((nameOrig == null) || (!name.equals(nameOrig.trim())))
				{
					FolderServices.renameFile(id, name, new DiskResourceRenameCallback(
							DiskResourceRenamedEvent.ResourceType.FILE, id, name));
				}
			}
		}
	}

	// ////////////////////////////////////////
	@Override
	public void handleOkClick()
	{
		doRename();
	}
}
