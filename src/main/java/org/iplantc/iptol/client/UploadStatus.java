package org.iplantc.iptol.client;

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IUploadStatus;
import org.iplantc.iptol.client.view.widgets.UploadPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class UploadStatus extends BaseUploadStatus 
{
	private com.extjs.gxt.ui.client.widget.Status percentageBar;
	private com.extjs.gxt.ui.client.widget.Status status;
	private Widget widget;
	private IptolConstants constants = (IptolConstants)GWT.create(IptolConstants.class);
	
	public UploadStatus(Widget widget) 
	{
		this.widget = widget;
	
		status = ((UploadPanel)widget).getStatusWidget();
		percentageBar = ((UploadPanel)widget).getPercentageWidget();
	    
		setProgressWidget(percentageBar);
	}

	@Override
	public Widget getWidget() 
	{
		return widget;
	};

	/**
	 * show/hide the modal dialog
	 */
	@Override
	public void setVisible(boolean visible) 
	{
		if (visible) 
		{
			((UploadPanel)widget).getUploadPanel().disable();
		} 
		else 
		{ 
			status.clearStatus(constants.selectNewFileToUpload());
			percentageBar.setText("");
			((UploadPanel)widget).getUploadPanel().enable();
		}	
	}
	
	/**
	 * eliminate unwanted/lengthy pop-up alerts
	 */
	@Override
	public void setError(String msg) 
	{
		setStatus(Status.ERROR);
	}
	
	@Override
	public void setProgress(int a,int b) 
	{
		if(b != 0) 
		{
			percentageBar.setText((a / b) * 100  + constants.percentComplete());
		} 
		else 
		{
			percentageBar.setText(0 + constants.percentComplete());
		}
	}
	
	@Override
	public IUploadStatus newInstance() 
	{
		return new UploadStatus(widget);
	}
}
