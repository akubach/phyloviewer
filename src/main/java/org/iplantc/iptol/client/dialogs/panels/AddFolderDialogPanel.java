package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.services.FolderCreateCallback;
import org.iplantc.iptol.client.services.FolderServices;

public class AddFolderDialogPanel extends IPlantPromptPanel 
{
	private String idWorkspace;
	private String idParent;
	
	//////////////////////////////////////////
	//constructor
	public AddFolderDialogPanel(String idWorkspace,String idParent) 
	{
		super(displayStrings.folderName(),250);
		
		this.idWorkspace = idWorkspace;
		this.idParent = idParent;
	}

	//////////////////////////////////////////
	//private methods
	private void doFolderAdd()
	{
		String name = field.getValue().trim();
		
		if(name != null)
		{
			name = name.trim();
		
			if(name.length() > 0)
			{
				FolderServices.createFolder(idWorkspace,name,idParent,new FolderCreateCallback(name));
			}
		}
	}

	//////////////////////////////////////////
	@Override
	public void handleOkClick() 
	{
		doFolderAdd();
	}
}
