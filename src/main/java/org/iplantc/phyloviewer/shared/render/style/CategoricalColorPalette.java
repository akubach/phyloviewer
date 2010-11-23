package org.iplantc.phyloviewer.shared.render.style;

import java.util.ArrayList;


/**
 * Maps values to a finite set of colors.
 */
public class CategoricalColorPalette implements IColorPalette {
	private final String[] colors;
	private final ArrayList<Object> map;
	//TODO a HashMap would probably be better here.  See if Adam thinks it's okay in this case.
	
	/**
	 * Create a new CategoricalColorPalette with a given set of colors, each
	 * specified in the same way as Canvas.setFillStyle() parameters. The last
	 * color in the array will be used as the fallback color when no more
	 * colors are available to be assigned.
	 * 
	 * @see org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas#setFillStyle(String)
	 */
	public CategoricalColorPalette(String[] colors) {
		this.colors = colors;
		map = new ArrayList<Object>(colors.length);
	}

	/**
	 * When a color is requested for a distinct value v (where
	 * !v.equals(x) for all values x already assigned a color), a new color gets
	 * assigned to o.  When all colors have been assigned, the last color is returned 
	 * for all new values.
	 * 
	 * @param value the value
	 * @return the color for that value, in the same format as {@link org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas#setFillStyle(String) Canvas.setFillStyle(String)}
	 */
	public String getColor(Object value) {
		int i = map.indexOf(value);
		if (i >= 0) {
			return colors[i];
		} else {
			if (map.size() < colors.length) {
				map.add(value);
				return colors[map.size() - 1];
			} else {
				return colors[colors.length - 1];
			}
		}
	}
	
	public int getNumValues() {
		return map.size();
	}
	
	public int getNumColors() {
		return colors.length;
	}
	
	/** 
	 * "Eleven colors that are almost never confused" (Boynton, 1989) 
	 * I got these color strings from Bernice's jpeg, so there may be some minor distortion from the jpeg compression. 
	 */
	public static class Boynton extends CategoricalColorPalette {
		public Boynton() {
			super(new String[] {"FE0000", "33CCF33", "FEFF00", "0166FE", "653300", "6700CE", "FFB3D7", "FF6501", "777777", "000000"});
		}
	}
	
	/** 
	 * "Segmented Rainbow color map - with iso-luminant colors", 9 colors
     * I got these color strings from Bernice's jpeg, so there may be some minor distortion from the jpeg compression. 
	 */
	public static class Rainbow9 extends CategoricalColorPalette {
		public Rainbow9() {
			super(new String[] {"9A32FF", "6969D9", "0099CD", "33CC33", "EEE943", "FF832F", "FEIC72", "FC46CC", "C623FA"});
		}
	}
	
	/** 
	 * "Segmented Rainbow color map - with iso-luminant colors", 15 colors
     * I got these color strings from Bernice's jpeg, so there may be some minor distortion from the jpeg compression. 
	 */
	public static class Rainbow15 extends CategoricalColorPalette {
		public Rainbow15() {
			super(new String[] {"A44BFF", "813AFF", "4966DE", "019FC4", "03AD92", "04A84F", "85D208", "DBD600", "FFA51D", "FF7F38", "C18445", "FC3E40", "FC3683", "FF30C9", "CA49FC"});
		}
	}
}


