package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.shared.render.style.IColorPalette;

public class ContinuousColorPalette implements IColorPalette {
	private double min;
	private int[] minColor = new int[4];
	
	//simplify getColor calculation by storing ranges instead of maxima
	private double valueRange;
	private int[] colorRange = new int[4];
	
	public ContinuousColorPalette(double minValue, double maxValue, int[] minColor, int[] maxColor) {
		//TODO validate inputs (min strictly < max, color arrays are right length and value ranges, etc.)
		this.min = minValue;
		this.valueRange = maxValue - minValue;
		this.minColor = minColor;
		
		for (int i = 0; i < 4; i++) {
			this.colorRange[i] = maxColor[i] - minColor[i];
		}
	}

	@Override
	public String getColor(Object value) {
		if (value instanceof Number) {
			return getColor(((Number) value).doubleValue());
		} else {
			throw new IllegalArgumentException("ContinuousColorPalette only accepts numerical values");
		}
	}
	
	public String getColor(double d) {
		//TODO Explore other color options. Not sure if interpolating a straight line through rgba space is best visually, but it's the obvious thing to try first.
		
		if (d <= min) {
			return rgbaString(minColor);
		} else if (d > min + valueRange) {
			return getColor(min + valueRange);
		} else {
			int[] color = new int[4];
			for (int i = 0; i < 4; i++) {
				color[i] = minColor[0] + (int)Math.round(((d - min)/valueRange) * colorRange[i]);
			}
			return rgbaString(color);
		}
	}

	private String rgbaString(int[] color) {
		double a = color[3]/255.0;
		return "rgba(" + color[0] + ", " + color[1] + ", " + color[2] + ", " + a + ")";
	}
}
