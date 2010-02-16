package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.events.FolderEvent;
import com.google.gwt.event.shared.HandlerManager;

public class RenameFolderDialogPanel extends IPlantPromptPanel 
{
	//////////////////////////////////////////
	//private variables
	private String nameOrig;
	
	//////////////////////////////////////////
	//constructor
	public RenameFolderDialogPanel(String nameOrig,HandlerManager eventbus) 
	{
		super(constants.folderName(),eventbus);
		
		this.nameOrig = nameOrig;	
		
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
					FolderEvent event = new FolderEvent(FolderEvent.Action.RENAME,name);
					eventbus.fireEvent(event);
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
