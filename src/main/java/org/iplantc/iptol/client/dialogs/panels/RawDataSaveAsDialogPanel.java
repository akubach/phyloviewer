package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.services.RawDataSaveAsCallback;
import org.iplantc.iptol.client.services.ViewServices;

public class RawDataSaveAsDialogPanel extends IPlantPromptPanel 
{
	protected String idWorkspace;
	protected FileIdentifier file;
	protected String data;	
	
	public RawDataSaveAsDialogPanel(String idWorkspace,FileIdentifier file,String data) 
	{
		super(displayStrings.fileName(),250);
		
		this.idWorkspace = idWorkspace;
		this.file = file;
		this.data = data;
		
		field.setValue(file.getFilename());
	}

	@Override
	public void handleOkClick() 
	{
		String nameOrig = file.getFilename();
		String name = field.getValue();
		
		if(name != null)
		{
			name = name.trim();
		
			if(name.length() > 0)
			{
				if((nameOrig == null) || (!name.equals(nameOrig.trim())))
				{
					//temp strings for readability
					String idParent = file.getParentId();
					String idFile = file.getFileId();
		
					ViewServices.saveAsRawData(idWorkspace,idParent,idFile,field.getValue(),data,new RawDataSaveAsCallback(idParent,idFile));
				}
			}
		}
	}
}
