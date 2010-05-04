package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.services.FolderRenameCallback;
import org.iplantc.de.client.services.FolderServices;

public class RenameFolderDialogPanel extends IPlantPromptPanel 
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	private String id;
	private String nameOrig;

	//////////////////////////////////////////
	//constructor
	public RenameFolderDialogPanel(String idWorkspace,String id,String nameOrig) 
	{
		super(displayStrings.folderName(),250);
		
		this.idWorkspace = idWorkspace;
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
				if((nameOrig == null) || (!name.equals(nameOrig)))
				{
					FolderServices.renameFolder(idWorkspace,id,name,new FolderRenameCallback(id,name));
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
