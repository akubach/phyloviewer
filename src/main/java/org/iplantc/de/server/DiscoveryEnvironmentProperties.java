package org.iplantc.de.server;

/**
 * Contains Discovery Environment Properties.
 * 
 * @author Donald A. Barre
 */
public class DiscoveryEnvironmentProperties
{

	private boolean securityEnabled = true;
	private static DiscoveryEnvironmentProperties deProperties;

	/**
	 * The Spring Framework will only instantiate this class once. We need to store the
	 * instance in a static member so we can access it from static methods.
	 */
	public DiscoveryEnvironmentProperties()
	{
		deProperties = this;
	}

	public void setSecurityEnabled(boolean securityEnabled)
	{
		this.securityEnabled = securityEnabled;
	}

	public boolean isSecurityEnabled()
	{
		return securityEnabled;
	}

	/**
	 * Need a static because DEServiceDispatcher needs to know is security is enabled or
	 * not. Unfortunately, the dispatcher is a servlet and the normal way to get the
	 * Spring application context doesn't work.
	 * 
	 * @return
	 */
	public static boolean isWebSecurityEnabled()
	{
		return deProperties.isSecurityEnabled();
	}
}
