package org.iplantc.de.client.jobs.contrast;

import java.util.HashMap;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.jobs.DataSelectedEvent;
import org.iplantc.de.client.jobs.Card;
import org.iplantc.de.client.models.JobParams;

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

	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT
			.create(DEDisplayStrings.class);

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
		data.setEnabled(false);
		// not required for this release
		data.setVisible(false);

		paramsPanel.add(statistics, formData);
		paramsPanel.add(contrasts, formData);
		paramsPanel.add(data, formData);

		return paramsPanel;
	}

	@Override
	public void reset() {
		contrasts.clear();
		data.clear();
		statistics.clear();
	}

	@Override
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
			// cannot select display data set alone
			if (statistics.getValue() == true || contrasts.getValue() == true) {
				data.setEnabled(true);
			} else {
				data.setValue(false);
				data.setEnabled(false);
			}
			isReadyForNext();
		}

	}

	@Override
	public void setJobParams(JobParams params) {
		
	}

}
