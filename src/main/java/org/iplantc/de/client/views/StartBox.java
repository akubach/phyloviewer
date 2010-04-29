package org.iplantc.de.client.views;

import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class StartBox extends BoxComponent 
{
	StartButton startBtn = new StartButton();

	  public StartBox() {
	    setId("ux-taskbar-start");
	  }

	  @Override
	  protected void doAttachChildren() {
	    super.doAttachChildren();
	    ComponentHelper.doAttach(startBtn);
	  }

	  @Override
	  protected void doDetachChildren() {
	    super.doDetachChildren();
	    ComponentHelper.doDetach(startBtn);
	  }

	  @Override
	  protected void onDisable() {
	    super.onDisable();
	    startBtn.disable();
	  }

	  @Override
	  protected void onEnable() {
	    super.onEnable();
	    startBtn.enable();
	  }

	  @Override
	  protected void onRender(Element target, int index) 
	  {
	    super.onRender(target, index);
	    setElement(DOM.createDiv(), target, index);

	    startBtn.render(getElement());
	  }

	  @Override
	  protected void onResize(int width, int height) {
	    super.onResize(width, height);
	    Size frameSize = el().getFrameSize();
	    startBtn.setSize(width - frameSize.width, height - frameSize.height);
	  }

	}