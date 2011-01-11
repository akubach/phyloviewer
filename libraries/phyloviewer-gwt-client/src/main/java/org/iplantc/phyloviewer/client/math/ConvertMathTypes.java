package org.iplantc.phyloviewer.client.math;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;

public class ConvertMathTypes {
	
	private ConvertMathTypes() {}

	public static Vector2 convertToVector2(JsVector2 jsVector) {
		return new Vector2(jsVector.getX(),jsVector.getY());
	}
	
	public static Box2D convertToBox2(JsBox2 jsBox) {
		Vector2 min = convertToVector2(jsBox.getMin());
		Vector2 max = convertToVector2(jsBox.getMax());
		return new Box2D(min,max);
	}
}
