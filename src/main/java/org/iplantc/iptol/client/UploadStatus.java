package org.iplantc.iptol.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import gwtupload.client.BaseUploadStatus;

public class UploadStatus extends BaseUploadStatus {
	protected PopupPanel box = new PopupPanel(false, true);

	public UploadStatus() {
		super();
		super.getWidget().addStyleName("upld-status");
		box.add(super.getWidget());
		DOM.setElementAttribute((com.google.gwt.user.client.Element) box
				.getElement().getFirstChild(), "class", "GWTUpld");
	}

	/**
	 * Returns an empty html widget, so, PopupPanel will never attached to the
	 * document by the user and it will be attached when show() is called
	 */
	@Override
	public Widget getWidget() {
		return new HTML();
	};

	/**
	 * show/hide the modal dialog
	 */
	@Override
	public void setVisible(boolean b) {
		if (b)
			box.center();
		else
			box.hide();
	}

	/**
	 * eliminate unwanted/lengthy pop-up alerts
	 */
	@Override
	public void setError(String msg) {
		setStatus(Status.ERROR);
	}
}
