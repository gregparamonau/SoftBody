package SoftBody2;

public class Vector2 {
	double x, y;
	
	//constructors:
		//normal
		//default (0 vector2)

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vector2() {
		this.x = 0;
		this.y = 0;
	}
	public Vector2(Vector2 in) {
		this.x = in.x;
		this.y = in.y;
	}
	
	//set method (avoids memory re-allocation)
	
	public void set(Vector2 in) {
		this.x = in.x;
		this.y = in.y;
	}
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	
	
	//personal add, sub, mult (alter original vector)
	public void add(Vector2 in) {
		this.x += in.x;
		this.y += in.y;
	}
	public void sub(Vector2 in) {
		this.x -= in.x;
		this.y -= in.y;
	}
	//instance method, returns a vector
	public Vector2 mult(double in) {
		return new Vector2(this.x * in, this.y * in);
	}
	public double l() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	public Vector2 rotate(double theta) {
		double cos = Math.cos(theta);
	    double sin = Math.sin(theta);
	    return new Vector2(this.x * cos - this.y * sin, this.x * sin + this.y * cos);
	}
	
	//static add, sub, mult
	
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.x + b.x, a.y + b.y);
	}
	public static Vector2 sub(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	public static Vector2 mult(Vector2 a, double mult) {
		return new Vector2(a.x * mult, a.y * mult);
	}
	public static double dist(Vector2 a, Vector2 b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	
	//functions:
		//dot (static)
	public static double dot(Vector2 a, Vector2 b) {
		return a.x * b.x + a.y * b.y;
	}
		//angle (signed)
	public static double angle(Vector2 a, Vector2 b) {
		
		if (a.l() < 1e-3 || b.l() < 1e-3) return 0;
		
		double theta = Math.acos(clamp(Vector2.dot(a, b) / a.l() / b.l(), -1, 1));
		
		if (Math.acos(clamp(Vector2.dot(a.rotate(0.00000000001), b) / a.l() / b.l(), -1, 1)) > theta) {
			return -theta;
		}
		return theta;
	}
		//reflect (static)
	public static Vector2 reflect(Vector2 in, Vector2 norm) {
		if (norm.l() < 1e-3) return in.mult(-1);
		return Vector2.sub(Vector2.mult(norm, 2 * Vector2.dot(in, norm)), in);
	}
		//project? (static)
	//not done for now
		//norm (instance)
	public Vector2 norm() {
		double l = this.l();
		
		if (l < 1e-6) return new Vector2(0, 0);
		return new Vector2(this.x / l, this.y / l);
	}
		//perp (instance)
	public Vector2 perp() {	
		
		if (this.l() < 1e-3) return new Vector2(0, 0);
		Vector2 temp = Vector2.mult(this, (double)1 / this.l());
		
		if (Double.isNaN(temp.x) || Double.isNaN((temp.y))) return new Vector2(0, 0);
		
		return new Vector2(temp.y, -temp.x);
	}
	
	
	//extra logic:
		//clamp
	public static double clamp(double in, double min, double max) {
		return Math.max(min, Math.min(in, max));
	}
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}
}
