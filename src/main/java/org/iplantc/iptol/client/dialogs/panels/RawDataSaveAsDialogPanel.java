package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.services.RawDataSaveAsCallback;
import org.iplantc.iptol.client.services.ViewServices;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class RawDataSaveAsDialogPanel extends IPlantPromptPanel 
{
	protected String idWorkspace;
	protected FileIdentifier file;
	protected String data;	
	private MessageBox wait;
	
	public RawDataSaveAsDialogPanel(String idWorkspace,FileIdentifier file,String data, MessageBox wait) 
	{
		super(displayStrings.fileName(),250);
		
		this.idWorkspace = idWorkspace;
		this.file = file;
		this.data = data;
		this.wait = wait;
		field.setValue(file.getFilename());
	}

	@Override
	public void handleOkClick() 
	{
		String name = field.getValue();
		
		if(name != null)
		{
			name = name.trim();
		
			if(name.length() > 0)
			{
				//temp strings for readability
				String idParent = file.getParentId();
				String idFile = file.getFileId();
				ViewServices.saveAsRawData(idWorkspace,idParent,idFile,field.getValue(),data,new RawDataSaveAsCallback(idParent,idFile,wait));
			}
		}
	}
}
