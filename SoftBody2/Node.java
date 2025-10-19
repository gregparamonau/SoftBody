package SoftBody2;

import java.awt.Color;
import java.awt.Graphics;

public class Node {
	Vector2 pos, last_pos, vel, force;
	double mass;
	
	//constructor
			//position
		//pos, vel, force
	
	public Node(double x, double y, double mass) {
		this.pos = new Vector2(x, y);
		this.last_pos = new Vector2(this.pos);
		this.vel = new Vector2();
		this.force = new Vector2();
		
		this.mass = mass;
	}
	
	//update (verlet integration or better)
	public void update(double dt) {
		
		double max_f = 50, v_drag = 0.85;
		
		//clamping force
		if (this.force.l() > max_f) this.force = this.force.norm().mult(max_f);
		
		/*Vector2 acceleration = this.force.mult(1.0 / this.mass);
		Vector2 position_diff = Vector2.sub(this.pos, this.last_pos).mult(v_drag);
		Vector2 next_pos = Vector2.add(
			    Vector2.add(this.pos, position_diff), // P_t + (P_t - P_{t-dt}) * v_drag
			    acceleration // + a * dt^2 (assuming dt^2 is 1 or absorbed into Force/Mass scaling)
			);*/
		
		//euler integration
		this.vel.add(this.force.mult(1.0 / this.mass).mult(dt));
		
		this.last_pos = new Vector2(this.pos);
		this.pos.add(this.vel.mult(v_drag));
		
		//this.force.set(new Vector2(0, 0));
		
		//intersection don't forget!
		
		//update force
		//update position
		//update last position
	}
	public void draw_node(Graphics g) {
		g.setColor(Color.magenta);
		Vector2 tempA = Vector2.add(this.pos, new Vector2(Base.screen_width / 2, Base.screen_height / 2));
		g.drawOval((int)(tempA.x - 5), (int)(tempA.y - 5), 10, 10);
	}
	public String toString() {
		return "POS: " + this.pos + " VEL: " + this.vel + " FORCE: " + this.force;
	}
	
}
