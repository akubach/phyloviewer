package org.iplantc.phyloviewer.client.tree.viewer.math;

public class Matrix33 {

	private double[][] _m=new double[3][3];

	public Matrix33() {
		this.set(1, 0, 0, 
				 0, 1, 0,
				 0, 0, 1);
	}
	
	public Matrix33(double m00, double m01, double m02, 
	                double m10, double m11, double m12,
	                double m20, double m21, double m22) {
		this.set(m00,m01,m02,
				 m10,m11,m12,
				 m20,m21,m22);
	}
	
	public static Matrix33 makeTranslate(double x, double y) {
		return new Matrix33(1,0,x,
				            0,1,y,
				            0,0,1);
	}
	
	public static Matrix33 makeScale(double sx, double sy ) {
		return new Matrix33(sx,0,0,
				            0,sy,0,
				            0,0,1);
	}
	
	public void set(double m00, double m01, double m02, 
			        double m10, double m11, double m12,
			        double m20, double m21, double m22 ) {	
		_m[0][0] = m00; _m[0][1] = m01;	_m[0][2] = m02;
		_m[1][0] = m10;	_m[1][1] = m11; _m[1][2] = m12;
		_m[2][0] = m20; _m[2][1] = m21; _m[2][2] = m22;
	}
		
	public Vector2 transform(Vector2 vector) {
		double x = _m[0][0] * vector.getX() + _m[0][1] * vector.getY() + _m[0][2];
		double y = _m[1][0] * vector.getX() + _m[1][1] * vector.getY() + _m[1][2];
		
		return new Vector2 ( x, y );
	}
	
	public Box2D transform(Box2D box) {
		Vector2 min = transform(box.getMin());
		Vector2 max = transform(box.getMax());
		return new Box2D(min, max);
	}
	
	public Matrix33 multiply(Matrix33 rhs) {
		double m00 = this._m[0][0] * rhs._m[0][0] + this._m[0][1] * rhs._m[1][0] + this._m[0][2] * rhs._m[2][0];
		double m01 = this._m[0][0] * rhs._m[0][1] + this._m[0][1] * rhs._m[1][1] + this._m[0][2] * rhs._m[2][1];
		double m02 = this._m[0][0] * rhs._m[0][2] + this._m[0][1] * rhs._m[1][2] + this._m[0][2] * rhs._m[2][2];
		
		double m10 = this._m[1][0] * rhs._m[1][0] + this._m[1][1] * rhs._m[1][0] + this._m[1][2] * rhs._m[2][0];
		double m11 = this._m[1][0] * rhs._m[0][1] + this._m[1][1] * rhs._m[1][1] + this._m[1][2] * rhs._m[2][1];
		double m12 = this._m[1][0] * rhs._m[0][2] + this._m[1][1] * rhs._m[1][2] + this._m[1][2] * rhs._m[2][2];
		
		double m20 = this._m[2][0] * rhs._m[1][0] + this._m[2][1] * rhs._m[1][0] + this._m[2][2] * rhs._m[2][0];
		double m21 = this._m[2][0] * rhs._m[0][1] + this._m[2][1] * rhs._m[1][1] + this._m[2][2] * rhs._m[2][1];
		double m22 = this._m[2][0] * rhs._m[0][2] + this._m[2][1] * rhs._m[1][2] + this._m[2][2] * rhs._m[2][2];
		
		return new Matrix33(m00,m01,m02,m10,m11,m12,m20,m21,m22);
	}
	
	public double determinant() {
		
		// Make three column vectors
		Vector3 v0 = new Vector3(_m[0][0], _m[1][0], _m[2][0]);
		Vector3 v1 = new Vector3(_m[0][1], _m[1][1], _m[2][1]);
		Vector3 v2 = new Vector3(_m[0][2], _m[1][2], _m[2][2]);
		
		Vector3 t = v1.cross(v2);
		return v0.dot(t);
	}
	
	public Matrix33 inverse() {
		double det = this.determinant();
		
		if ( det == 0.0 )
			throw new RuntimeException("This matrix is not invertable");
		
		// Make three column vectors
		Vector3 v0 = new Vector3(_m[0][0], _m[1][0], _m[2][0]);
		Vector3 v1 = new Vector3(_m[0][1], _m[1][1], _m[2][1]);
		Vector3 v2 = new Vector3(_m[0][2], _m[1][2], _m[2][2]);
		
		// These are row vectors.
		Vector3 t0 = v1.cross(v2);
		Vector3 t1 = v2.cross(v0);
		Vector3 t2 = v0.cross(v1);
		
		double oneOverDet = 1.0 / det;
		return new Matrix33 ( oneOverDet * t0.getX(), oneOverDet * t0.getY(), oneOverDet * t0.getZ(),
				              oneOverDet * t1.getX(), oneOverDet * t1.getY(), oneOverDet * t1.getZ(),
				              oneOverDet * t2.getX(), oneOverDet * t2.getY(), oneOverDet * t2.getZ());
	}
	
	public double get(int row, int col) { return _m[row][col]; }

	public double getTranslationX() { return _m[0][2]; }
	public double getTranslationY() { return _m[1][2]; }	
	public double getScaleX() { return _m[0][0]; }
	public double getScaleY() { return _m[1][1]; }
}
