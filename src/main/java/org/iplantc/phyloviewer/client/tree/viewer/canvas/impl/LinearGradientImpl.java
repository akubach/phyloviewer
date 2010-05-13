/*
 * Copyright 2008-2009 Oliver Zoran
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iplantc.phyloviewer.client.tree.viewer.canvas.impl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * The default implementation of the linear gradient style.
 */
public class LinearGradientImpl extends GradientImpl {

	public LinearGradientImpl(double x0, double y0, double x1, double y1,
			JavaScriptObject context) {
		createNativeGradientObject(x0, y0, x1, y1, context);
	}

	private native void createNativeGradientObject(double x0, double y0, double x1, double y1,
			JavaScriptObject context) /*-{
		this.@org.iplantc.phyloviewer.client.tree.viewer.canvas.impl.GradientImpl::gradient =
			context.createLinearGradient(x0, y0, x1, y1);
	}-*/;
}
