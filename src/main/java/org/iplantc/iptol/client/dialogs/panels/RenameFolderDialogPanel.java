package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.services.FolderServices;
import org.iplantc.iptol.client.services.FolderUpdater;

import com.google.gwt.event.shared.HandlerManager;

public class RenameFolderDialogPanel extends IPlantPromptPanel 
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	private String id;
	private String nameOrig;

	//////////////////////////////////////////
	//constructor
	public RenameFolderDialogPanel(String idWorkspace,String id,String nameOrig,HandlerManager eventbus) 
	{
		super(displayStrings.folderName(),eventbus);
		
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
					FolderServices.renameFolder(idWorkspace,id,name,new FolderUpdater(eventbus));
					
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
