package org.iplantc.iptol.client.JobConfiguration.contrast;

import java.util.HashMap;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.Card;
import org.iplantc.iptol.client.JobConfiguration.DataSelectedEvent;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.core.client.GWT;

/**
 * @author sriram Provides UI to select params for the independent contrast job
 */

public class SelectOptionalParams extends Card {

	private FormPanel paramsPanel;
	private FormData formData;
	private CheckBox statistics;
	private CheckBox contrasts;
	private CheckBox data;

	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
			.create(IptolDisplayStrings.class);

	public SelectOptionalParams(int step) {
		this.step = step;
		paramsPanel = new FormPanel();
		formData = new FormData("-20");
	}

	@Override
	public FormPanel assembleView() {
		paramsPanel.setHeaderVisible(false);
		paramsPanel.setLayout(new VBoxLayout());
		paramsPanel.setFrame(true);
		paramsPanel.setWidth(400);
		paramsPanel.setHeight(200);

		// Print out correlations and regressions
		statistics = new CheckBox();
		statistics.setBoxLabel(displayStrings.printCorrelationsRegressions());
		statistics.addListener(Events.OnClick, new CheckBoxListener());

		// Print out contrasts
		contrasts = new CheckBox();
		contrasts.setBoxLabel(displayStrings.printContrasts());
		contrasts.addListener(Events.OnClick, new CheckBoxListener());

		// Print out the data at start of run
		data = new CheckBox();
		data.setBoxLabel(displayStrings.printDataSets());
		data.addListener(Events.OnClick, new CheckBoxListener());

		paramsPanel.add(statistics, formData);
		paramsPanel.add(contrasts, formData);
		paramsPanel.add(data, formData);

		// set default selection values
		isReadyForNext();

		return paramsPanel;
	}

	public void isReadyForNext() {
		DataSelectedEvent event = null;
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put(displayStrings.printCorrelationsRegressions(), statistics
				.getValue());
		param.put(displayStrings.printContrasts(), contrasts.getValue());
		param.put(displayStrings.printDataSets(), data.getValue());
		EventBus eventbus = EventBus.getInstance();
		event = new DataSelectedEvent(step, true, param);
		eventbus.fireEvent(event);
	}

	class CheckBoxListener implements Listener<BaseEvent> {
		@Override
		public void handleEvent(BaseEvent be) {
			isReadyForNext();
		}

	}

}
