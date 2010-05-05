package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.services.FileRenameCallback;
import org.iplantc.de.client.services.FolderServices;

public class RenameFileDialogPanel  extends IPlantPromptPanel 
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String nameOrig;

	//////////////////////////////////////////
	//constructor
	public RenameFileDialogPanel(String id,String nameOrig) 
	{
		super(displayStrings.fileName(),250);
		
		this.nameOrig = nameOrig;	
		this.id = id;
		
		field.setValue(nameOrig);
	}

	//////////////////////////////////////////
	//private methods
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
					FolderServices.renameFile(id,name,new FileRenameCallback(id,name));
				}
			}
		}
	}
		
	//////////////////////////////////////////
	@Override
	public void handleOkClick() 
	{
		doRename();
	}
}
