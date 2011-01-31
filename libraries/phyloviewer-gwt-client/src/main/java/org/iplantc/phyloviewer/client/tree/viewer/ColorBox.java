package org.iplantc.phyloviewer.client.tree.viewer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A TextBox with the css class "color" added, for attaching a color picker widget to. The background
 * color of the box also updates to reflect the current value. Basic color names are automatically mapped
 * to their hex RGB values.
 */
public class ColorBox extends TextBox
{
	static final Map<String, String> colorNameMap = new HashMap<String,String>(16);
	
	/**
	 * Initialize color map with basic CSS colors from http://www.w3.org/TR/2010/PR-css3-color-20101028/#html4
	 */
	static 
	{
		colorNameMap.put("black", "#000000");
		colorNameMap.put("silver", "#C0C0C0");
		colorNameMap.put("gray", "#808080");
		colorNameMap.put("white", "#FFFFFF");
		colorNameMap.put("maroon", "#800000");
		colorNameMap.put("red", "#FF0000");
		colorNameMap.put("purple", "#800080");
		colorNameMap.put("fuchsia", "#FF00FF");
		colorNameMap.put("green", "#008000");
		colorNameMap.put("lime", "#00FF00");
		colorNameMap.put("olive", "#808000");
		colorNameMap.put("yellow", "#FFFF00");
		colorNameMap.put("navy", "#000080");
		colorNameMap.put("blue", "#0000FF");
		colorNameMap.put("teal", "#008080");
		colorNameMap.put("aqua", "#00FFFF");
	}
	
	public ColorBox()
	{
		this.addStyleName("color");
		
		this.addValueChangeHandler(new ValueChangeHandler<String>()
		{	
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				String value = event.getValue();
				value = validate(event.getValue());
				
				if (value == null || value.isEmpty()) {
					ColorBox.this.getElement().setAttribute("style", "");
					return;
				}
				
				String foreground = getForegroundColor(value); //readable foreground color, given background
				ColorBox.this.getElement().setAttribute("style", "background-color:" + value + "; color:" + foreground + ";");
			}
		});
	}
	
	private String getForegroundColor(String backgroundHexRGB)
	{
		String hex = backgroundHexRGB.substring(1);
		int r = Integer.parseInt(hex.substring(0, 2), 16);
		int g = Integer.parseInt(hex.substring(2, 4), 16);
		int b = Integer.parseInt(hex.substring(4), 16);
		
		//grayscale value from http://en.wikipedia.org/wiki/Luminance_(relative)
		double y = 0.2126 * r + 0.7152 * g + 0.0722 * b;
		
		String foreground = y > 128 ?  "#000000" : "#FFFFFF";
		return foreground;
	}
	
	private String validate(String color)
	{
		if (color == null)
		{
			return null;
		}
		
		if(colorNameMap.containsKey(color))
		{
			return colorNameMap.get(color);
		}
		
		if (!color.matches("^#([0-9a-fA-F]{2}){3}|([0-9a-fA-F]){3}$"))
		{
			return null;
		}
		
		return color;
	}
}
