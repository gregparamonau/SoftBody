package SoftBody2;

import java.awt.Color;
import java.awt.Graphics;

public class Line {
	Vector2 a, b;
	
	public Line(Vector2 a, Vector2 b) {
		this.a = a;
		this.b = b;
	}
	//norm
	public Vector2 norm() {
		return Vector2.sub(b, a).perp().norm();
	}
	
	//find_node_on_line
	public static Vector2 find_node_on_line(Line a, Vector2 in) {
		
		
		Vector2 ac = Vector2.sub(a.b, a.a);
		Vector2 ab = Vector2.sub(in, a.a);
		
		 double lengthSq = ac.l() * ac.l();
		    if (lengthSq < 1E-6) {
		    	return a.a;
		    }
		
		Vector2 p = Vector2.add(a.a, ac.mult(clamp(Vector2.dot(ac, ab) / ac.l() / ac.l(), 0, 1)));
		
		return p;
	}
	
	public static boolean horiz_intersect(Line a, Line b) {
		//if b horizontal, return false
		if (b.a.y == b.b.y) return false;
		
		
		//assume a is horizontal
		Vector2 bounds = b.y_bounds();
		
		double ta = (a.a.y - b.a.y) / (b.b.y - a.a.y);
		double x = ta * b.b.x + (1 - ta) * a.a.x;
		
		//
		
		//
		return bounds.x < a.a.y && bounds.y > a.a.y && x > a.a.x;
		
	}
	
	public static boolean line_intersect(Line a, Line b) {
		boolean vertical = (Math.abs(b.a.x - b.b.x) < 1e-3);
		double m1 = (a.a.y - a.b.y) / (a.a.x - a.b.x);
		double b1 = (a.a.y - m1 * a.a.x);
		
		if (vertical) {
			//double x_node = b.a.x;
			double y_node = m1 * b.a.x + b1;
			return (Math.min(b.a.y, b.b.y) <= y_node && y_node <= Math.max(b.a.y, b.b.y) && Math.min(a.a.y, a.b.y) <= y_node && y_node <= Math.max(a.a.y, a.b.y));
		}
		double m2 = (b.a.y - b.b.y) / (b.a.x - b.b.x);
		double b2 = (b.a.y - m2 * b.a.x);
		
		double x_node = (b2 - b1) / (m1 - m2);
		//double y_node = (m1 * x_node) + b1;
		
		return (Math.min(a.a.x, a.b.x) <= x_node && x_node <= Math.max(a.a.x, a.b.x) && Math.min(b.a.x, b.b.x) <= x_node && x_node <= Math.max(b.a.x, b.b.x));
	}
	
	public Vector2 y_bounds() {
		return new Vector2(Math.min(this.a.y, this.b.y), Math.max(this.a.y, this.b.y));
	}
	//etc.


	//extra logic:
	public static double clamp(double in, double min, double max) {
		return Math.max(min, Math.min(in, max));
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.cyan);
		Vector2 tempA = Vector2.add(this.a, new Vector2(Base.screen_width / 2, Base.screen_height / 2));
		Vector2 tempB = Vector2.add(this.b, new Vector2(Base.screen_width / 2, Base.screen_height / 2));
		
		g.drawLine((int)tempA.x, (int)tempA.y, (int)tempB.x, (int)tempB.y);
	}
	
}
