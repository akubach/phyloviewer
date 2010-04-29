package org.iplantc.iptol.client.tree.viewer;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;

public class Log extends Composite {
	
	private TextArea _log = new TextArea();
	private static Log _instance;
	
	private Log() {
		this.initWidget(_log);
		
		_log.setCharacterWidth(100);
		_log.setVisibleLines(10);
	}
	
	public static Log instance() {
		if (_instance==null)
			_instance = new Log();
		return _instance;
	}

	public void addMessage(String message) {
		_log.setText(_log.getText() + "\n" + message);
	}
}
