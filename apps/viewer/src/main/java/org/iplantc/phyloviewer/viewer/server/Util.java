package org.iplantc.phyloviewer.viewer.server;

import javax.servlet.http.HttpServletRequest;

public class Util
{
	/**
	 * Simulates a given delay if the request originates on local host.
	 * 
	 * @param request the value of getThreadLocalRequest() from a RemoteServiceServlet. Should be null if
	 *            the RemoteServiceServlet method was called from another object in the same JVM (another
	 *            servlet for example) (in which case we don't simulate delay) or "127.0.0.1" if the
	 *            client is on the local machine (in which case we do simulate delay).
	 * @param millis delay length
	 */
	public static void simulateDelay(HttpServletRequest request, long millis) {
		if (request != null && request.getRemoteHost().equals("127.0.0.1"))
		{
			try {
				Thread.sleep(millis);
				System.out.println("Simulating " + millis + " ms delay");
			} catch (InterruptedException e) {
			}
		}
	}
}
