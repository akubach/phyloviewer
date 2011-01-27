package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.shared.render.RenderPreferences;

public interface HasRenderPreferences
{
	public void setRenderPreferences(RenderPreferences rp);
	public RenderPreferences getRenderPreferences();
}
