package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.events.FolderEvent;
import com.google.gwt.event.shared.HandlerManager;

public class AddFolderDialogPanel extends IPlantPromptPanel 
{
	//////////////////////////////////////////
	//constructor
	public AddFolderDialogPanel(HandlerManager eventbus) 
	{
		super(constants.folderName(),eventbus);		
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
				FolderEvent event = new FolderEvent(FolderEvent.Action.CREATE,name);
				eventbus.fireEvent(event);			
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
