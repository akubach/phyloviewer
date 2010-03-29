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
	}

	@Override
	public void handleOkClick() 
	{
		ViewServices.saveAsRawData(idWorkspace, file.getParentId(), file.getFileId(),field.getValue(),data,new RawDataSaveAsCallback());		
	}
}
