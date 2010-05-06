package org.iplantc.de.client.jobs;

import org.iplantc.de.client.models.JobParams;

import com.google.gwt.user.client.ui.Widget;

/**
 * A basic card in wizard. A wizard is made up of set of cards.
 * 
 * @author sriram 
 */
public abstract class Card
{

	protected int step;

	public int getStep()
	{
		return step;
	}

	public void setStep(int step)
	{
		this.step = step;
	}

	public abstract void setJobParams(JobParams params);

	public abstract Widget assembleView();

	public abstract void isReadyForNext();

	public abstract void reset();

}
