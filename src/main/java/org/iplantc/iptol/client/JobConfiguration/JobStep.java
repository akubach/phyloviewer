package org.iplantc.iptol.client.JobConfiguration;

/**
 * 
 * @author sriram Represents a JobStep
 */
public class JobStep {

	private static final long serialVersionUID = 5835114258342501112L;
	private int stepno;
	private String name;
	private boolean defaultEnable;
	private boolean comlpete;

	/**
	 * create new instance of JobStep
	 * 
	 * @param stepno
	 *            the step no
	 * @param name
	 *            step name
	 * @param defaultEnable
	 *            should step be enabled by default
	 */
	public JobStep(int stepno, String name, boolean defaultEnable) {
		setStepno(stepno);
		setName(name);
		setDefaultEnable(defaultEnable);
		this.setComlpete(false);
	}

	public int getStepno() {
		return stepno;
	}

	public void setStepno(int stepno) {
		this.stepno = stepno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultEnable() {
		return defaultEnable;
	}

	public void setDefaultEnable(boolean defaultEnable) {
		this.defaultEnable = defaultEnable;
	}

	public void setComlpete(boolean comlpete) {
		this.comlpete = comlpete;
	}

	public boolean isComlpete() {
		return comlpete;
	}

}