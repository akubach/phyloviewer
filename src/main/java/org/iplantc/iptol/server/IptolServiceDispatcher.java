package org.iplantc.iptol.server;

import org.iplantc.iptol.client.IptolService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class IptolServiceDispatcher extends RemoteServiceServlet implements IptolService  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getServiceData(String ServiceId) {
		String json = null;
		try {
			json = ServiceHelper.call("http://localhost:14444/trees",null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("json==>" + json);
		return json;
	}

}
