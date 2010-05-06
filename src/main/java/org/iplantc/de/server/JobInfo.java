package org.iplantc.de.server;

public class JobInfo
{

	private String id;
	private String description;
	private String state;
	private String submitTime;
	private String startTime;
	private String endTime;

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
		if(this.description == null)
		{
			this.description = "";
		}
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getSubmitTime()
	{
		return submitTime;
	}

	public void setSubmitTime(String submitTime)
	{
		this.submitTime = submitTime;
		if(this.submitTime == null)
		{
			this.submitTime = "";
		}
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
		if(this.startTime == null)
		{
			this.startTime = "";
		}
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
		if(this.endTime == null)
		{
			this.endTime = "";
		}
	}
}
