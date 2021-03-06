/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.render.style.BranchStyle;
import org.iplantc.phyloviewer.shared.render.style.GlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.LabelStyle;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;

public class Defaults {

	final public static double POINT_SIZE=6;
	final public static String POINT_COLOR="#000000";
	
	final public static String LINE_COLOR="#000000";
	
	final public static String TEXT_COLOR="#000000";
	
	final public static String TRIANGLE_FILL_COLOR="#99FF99";
	final public static String TRIANGLE_OUTLINE_COLOR="#19B319";
	
	final public static String OVERVIEW_FILL_COLOR="rgba(51, 51, 220, 0.3)";
	final public static String OVERVIEW_OUTLINE_COLOR="rgba(51, 51, 220, 1.0)";
	
	final public static IStyle DEFAULT_STYLE=new Style("default", new NodeStyle(Defaults.POINT_COLOR, Defaults.POINT_SIZE),
			new LabelStyle(Defaults.TEXT_COLOR), 
			new GlyphStyle(Defaults.TRIANGLE_FILL_COLOR, Defaults.TRIANGLE_OUTLINE_COLOR, 1.0),
			new BranchStyle(Defaults.LINE_COLOR, 1.0));
}
