package org.iplantc.de.client.views.taskbar;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.core.Template;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class StartButton extends Button 
{
	private StartMenu startMenu;

	public StartButton(StartMenuComposer composer) 
	{
		setText("Start");
		setId("ux-startbutton");
		setIcon(IconHelper.createStyle("start", 23, 23));
		setMenuAlign("bl-tl");

		startMenu = new StartMenu();
		setMenu(startMenu);

		if(composer != null)
		{
			composer.compose(startMenu);
		}
		
		template = new Template(getButtonTemplate());
	}

	@Override
	public void setIcon(AbstractImagePrototype icon) 
	{
		super.setIcon(icon);
		
		if(rendered) 
		{
			if(buttonEl.selectNode("img") != null) 
			{
				buttonEl.selectNode("img").remove();
			}

			if(icon != null) 
			{
				buttonEl.setPadding(new Padding(7, 0, 7, 28));
				Element e = (Element) icon.createElement().cast();
				buttonEl.insertFirst(e);
	        
				El.fly(e).makePositionable(true);
				String align = "b-b";
				if(getIconAlign() == IconAlign.BOTTOM) 
				{
					align = "b-b";
				} 
				else if(getIconAlign() == IconAlign.TOP) 
				{
					align = "t-t";
				} 
				else if(getIconAlign() == IconAlign.LEFT) 
				{
					align = "l-l";
				} 
				else if(getIconAlign() == IconAlign.RIGHT) 
				{
					align = "r-r";
				}
				
				El.fly(e).alignTo(buttonEl.dom, align, null);
			}
		}
	}

	@Override
	protected void autoWidth() 
	{
	}

	@Override
	protected void onResize(int width, int height) 
	{
		super.onResize(width, height);
	    
		buttonEl.setSize(width - 20, height, true);
	}

	private native String getButtonTemplate() /*-{
		return [
	    '<table border="0" cellpadding="0" cellspacing="0" class="x-btn-wrap"><tbody><tr>',
	    '<td class="ux-startbutton-left"><i>&#160;</i></td><td class="ux-startbutton-center"><em unselectable="on"><button class="x-btn-text" type="{1}" style="height:30px;">{0}</button></em></td><td class="ux-startbutton-right"><i>&#160;</i></td>',
	    '</tr></tbody></table>'
	    ].join("");
	}-*/;
}