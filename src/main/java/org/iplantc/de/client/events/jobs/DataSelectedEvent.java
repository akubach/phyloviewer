package org.iplantc.de.client.events.jobs;

import java.util.HashMap;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents data was selected in step and the user needs to be notified.
 * 
 * @author sriram
 * 
 */
public class DataSelectedEvent extends GwtEvent<DataSelectedEventHandler>
{

	private boolean selected;
	private int step;
	private HashMap<String,Object> data;

	public static final GwtEvent.Type<DataSelectedEventHandler> TYPE = new GwtEvent.Type<DataSelectedEventHandler>();

	/**
	 * create a new instance of DataSelectedEvent
	 * 
	 * @param step step number
	 * @param selected whether data was selected or deselected
	 * @param data the data itesef if one is selected
	 */
	public DataSelectedEvent(int step, boolean selected, HashMap<String,Object> data)
	{
		this.setSelected(selected);
		this.setStep(step);
		this.setData(data);
	}

	@Override
	protected void dispatch(DataSelectedEventHandler handler)
	{
		handler.onDataSelected(this);
	}

	@Override
	public Type<DataSelectedEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setStep(int step)
	{
		this.step = step;
	}

	public int getStep()
	{
		return step;
	}

	public void setData(HashMap<String,Object> data)
	{
		this.data = data;
	}

	public HashMap<String,Object> getData()
	{
		return data;
	}

}
