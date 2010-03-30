package org.iplantc.iptol.client.dialogs;

import org.iplantc.iptol.client.dialogs.panels.UploadPanel;

import gwtupload.client.IUploader;

import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;

public class FileUploadDialog extends Dialog 
{
	public FileUploadDialog(String prompt,String servletPath,Point p,IUploader.OnFinishUploaderHandler handler)
	{
		UploadPanel upload_panel = new UploadPanel(prompt,servletPath,handler);
		upload_panel.assembleComponents();
		add(upload_panel);
		
		setPagePosition(p);
		setHeaderVisible(true);
		setHeading("Upload A File");
		setButtons(Dialog.CANCEL);
		setHideOnButtonClick(true);		
		setWidth(450);
	}
}
