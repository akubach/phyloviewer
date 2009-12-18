package org.iplantc.iptol.client;

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IUploadStatus;

import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;

public class UploadStatus extends BaseUploadStatus {
	
   
	com.extjs.gxt.ui.client.widget.Status percentageBar;
	com.extjs.gxt.ui.client.widget.Status status;
	ToolBar toolBar;
	Widget widget;
	public UploadStatus(Widget widget) {
		this.widget = widget;
		status = ((UploadPanel)widget).getStatusWidget();
		percentageBar = ((UploadPanel)widget).getPercentageWidget();
	    setProgressWidget(percentageBar);
	}

	@Override
	public Widget getWidget() {
		return widget;
	};

	/**
	 * show/hide the modal dialog
	 */
	@Override
	public void setVisible(boolean v) {
            if (v) {
            	 ((UploadPanel)widget).getUploadPanel().disable();
            } else { 
            	status.clearStatus("Select a new file to upload");
            	percentageBar.setText("");
            	((UploadPanel)widget).getUploadPanel().enable();
            }
	}
	
	/**
	 * eliminate unwanted/lengthy pop-up alerts
	 */
	@Override
	public void setError(String msg) {
		setStatus(Status.ERROR);
	}
	
	@Override
	public void setProgress(int a, int b) {
		if(b!=0) {
			percentageBar.setText((a/b)*100  + "% Complete" );
		} else {
			percentageBar.setText(0 + "% Complete" );
		}
	}
	
	@Override
	public IUploadStatus newInstance() {
		return new UploadStatus(widget);
	}
}
