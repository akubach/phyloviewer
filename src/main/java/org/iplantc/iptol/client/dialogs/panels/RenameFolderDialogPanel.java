package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.events.FolderEvent;
import com.google.gwt.event.shared.HandlerManager;

public class RenameFolderDialogPanel extends IPlantPromptPanel 
{
	//////////////////////////////////////////
	//private variables
	private String nameOrig;
	private String id;
	
	//////////////////////////////////////////
	//constructor
	public RenameFolderDialogPanel(String id,String nameOrig,HandlerManager eventbus) 
	{
		super(displayStrings.folderName(),eventbus);
		
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
					FolderEvent event = new FolderEvent(FolderEvent.Action.RENAME,name,id);
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
