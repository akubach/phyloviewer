package org.iplantc.de.client.models;

/**
 * Represents a JobStep
 * 
 * @author sriram 
 */
public class JobStep
{

	private static final long serialVersionUID = 5835114258342501112L;
	private int stepno;
	private String name;
	private boolean defaultEnable;
	private boolean complete;

	/**
	 * create new instance of JobStep
	 * 
	 * @param stepno the step no
	 * @param name step name
	 * @param defaultEnable should step be enabled by default
	 */
	public JobStep(int stepno, String name, boolean defaultEnable)
	{
		setStepno(stepno);
		setName(name);
		setDefaultEnable(defaultEnable);
		this.setComplete(false);
	}

	public int getStepno()
	{
		return stepno;
	}

	public void setStepno(int stepno)
	{
		this.stepno = stepno;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isDefaultEnable()
	{
		return defaultEnable;
	}

	public void setDefaultEnable(boolean defaultEnable)
	{
		this.defaultEnable = defaultEnable;
	}

	public void setComplete(boolean comlpete)
	{
		this.complete = comlpete;
	}

	public boolean isComplete()
	{
		return complete;
	}

}