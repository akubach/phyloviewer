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

package org.iplantc.phyloviewer.client.tree.viewer.canvas;

/**
 * Listener interface to be used to register an optional callback method to
 * be notified when the actual image has been loaded.
 */
public interface ImageListener {

	/**
	 * A method that will be invoked once the image data has been properly
	 * loaded by the browser. Implement it to be notified when the image
	 * has been loaded successfully and is ready to use.
	 * 
	 * @param image a handle to this image object
	 */
	public void onLoadingComplete(Image image);
}
