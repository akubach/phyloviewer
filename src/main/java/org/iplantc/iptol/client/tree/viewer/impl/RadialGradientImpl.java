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

package org.iplantc.iptol.client.tree.viewer.impl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * The default implementation of the radial gradient style.
 */
public class RadialGradientImpl extends GradientImpl {

	public RadialGradientImpl(double x0, double y0, double r0, double x1,
			double y1, double r1, JavaScriptObject context) {
		createNativeGradientObject(x0, y0, r0, x1, y1, r1, context);
	}

	private native void createNativeGradientObject(double x0, double y0, double r0, double x1,
			double y1, double r1, JavaScriptObject context) /*-{
		this.@org.iplantc.iptol.client.tree.viewer.impl.GradientImpl::gradient =
			context.createRadialGradient(x0, y0, r0, x1, y1, r1);
	}-*/;
}
