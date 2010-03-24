package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.services.FileRenameCallback;
import org.iplantc.iptol.client.services.FolderServices;

import com.google.gwt.event.shared.HandlerManager;

public class RenameFileDialogPanel  extends IPlantPromptPanel 
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String nameOrig;

	//////////////////////////////////////////
	//constructor
	public RenameFileDialogPanel(String id,String nameOrig,HandlerManager eventbus) 
	{
		super(displayStrings.fileName(),eventbus);
		
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
					FolderServices.renameFile(id,name,new FileRenameCallback(eventbus,id,name));
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
