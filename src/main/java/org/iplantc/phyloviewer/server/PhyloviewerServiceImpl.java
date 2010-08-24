package org.iplantc.phyloviewer.server;

import java.util.HashMap;
import java.util.Map;

import org.iplantc.phyloviewer.client.services.PhyloviewerService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PhyloviewerServiceImpl extends RemoteServiceServlet implements PhyloviewerService {
	private final Map<Class<? extends Action<?>>, PhyloviewerService> handlers = new HashMap<Class<? extends Action<?>>, PhyloviewerService>();

	@Override
	public <T extends Response> T execute(Action<T> action) {
		return getHandler(action).execute(action);
	}
	
	private <T extends Response> PhyloviewerService getHandler(Action<T> action) {
		return handlers.get(action.getClass());
	}

	public static class FooAction implements Action<FooResponse> {
		
	}
	
	public static class FooResponse implements Response {
		
	}
}
