package SoftBody2;

import java.awt.Color;
import java.awt.Graphics;

public class Spring {
	Node a, b;
	Vector2 hook;
	double rest_length, k;
	boolean hooked;
	//constructors
		//Node Node
		//Node Vector2 (hooked)
	
	public Spring(Node a, Node b, double k) {
		this.a = a;
		this.b = b;
		
		this.k = k;
		
		this.rest_length = this.length();
		
		this.hooked = false;
		
	}
	
	public Spring(Node a, Vector2 b, double k) {
		this.a = a;
		this.hook = b;
		
		this.k = k;
		
		this.hooked = true;
		
		this.rest_length = this.length();
	}
	
	
	//length()
	public double length() {
		if (this.hooked) return Vector2.dist(this.a.pos, hook);
		
		return Vector2.dist(this.a.pos, this.b.pos);
	}
	
	//update (apply forces to nodes)
	
	public void update() {
		if (this.hooked) {
			Vector2 a = Vector2.sub(this.hook, this.a.pos);
			if (a.l() < 1E-3) return;
			Vector2 dir = a.norm();
			
			double kd = 1.75;
			
			this.a.force.add(dir.mult((a.l() - this.rest_length) * this.k - Vector2.dot(dir, this.a.vel) * kd));
			return;
			//return;
		}
		
		double ds = this.length() - this.rest_length, kd = 1.75;//kd = damping factor
		Vector2 dir = Vector2.sub(this.b.pos, this.a.pos).norm();
		//spring force
		Vector2 Fs = dir.mult(ds * k);
		//drag force
		//TBD
		Vector2 Fd = dir.mult(Vector2.dot(dir, Vector2.sub(this.b.vel, this.a.vel)) * kd);
		
		Vector2 F = Vector2.add(Fs, Fd);
		//Vector2 Fd = ;//Vector2.add(this.a.vel.mult(-), Fs)
		
		this.a.force.add(F);
		this.b.force.add(F.mult(-1));
	}
	
	public void draw(Graphics g) {
		
		
		g.setColor(Color.white);
		Vector2 tempA = Vector2.add(this.a.pos, new Vector2(Base.screen_width / 2, Base.screen_height / 2));
		
		if (this.hooked) {
			Vector2 tempB = Vector2.add(this.hook, new Vector2(Base.screen_width / 2, Base.screen_height / 2));
			g.drawLine((int)tempA.x, (int)tempA.y, (int)tempB.x, (int)tempB.y);
			return;
		}
		Vector2 tempB = Vector2.add(this.b.pos, new Vector2(Base.screen_width / 2, Base.screen_height / 2));

		
		g.drawLine((int)tempA.x, (int)tempA.y, (int)tempB.x, (int)tempB.y);


	}
	//damping method?
		// Fd = (B - A)/|B - A| * (V_B - V_A) k_d
		//clamp force
}
