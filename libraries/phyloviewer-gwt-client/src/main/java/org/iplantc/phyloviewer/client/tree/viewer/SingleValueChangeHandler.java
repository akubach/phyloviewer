package org.iplantc.phyloviewer.client.tree.viewer;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public abstract class SingleValueChangeHandler<T> implements ValueChangeHandler<T>
{
	HandlerRegistration registration;
	
	public void attachTo(HasValue<T> widget)
	{
		detach();
		registration = widget.addValueChangeHandler(this);
	}
	
	public void detach()
	{
		if (registration != null)
		{
			registration.removeHandler();
		}
		
		registration = null;
	}
}
