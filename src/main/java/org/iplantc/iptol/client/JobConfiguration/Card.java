package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author sriram A basic card in wizard. A wizard is made up of set of cards.
 */
public abstract class Card {

	protected int step;

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public abstract Widget assembleView();

	public void setJobParams(JobParams params) {

	}

}
