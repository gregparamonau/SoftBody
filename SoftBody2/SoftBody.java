package SoftBody2;

import java.awt.Color;
import java.awt.Graphics;
public class SoftBody {
	//ISSUES TO FIX:
		//squares collapsing in on each other
		//performance needs to be offloaded to a separate thread.
	Vector2 pos;
	
	Polygon pol;
	
	Color fill;
	
	//'base_rest_pos' is the very default non-rotated mesh that the shape has --> immutable
	//'rest_pos' is the rotated and moved mesh that the shape is actually pulled towards
	Vector2[] base_rest_pos, rest_pos;
	Node[] nodes;
	
	Spring[] springs;
	
	public double angle, rest_volume, k = 100;
	//simulation substeps
	final int num = 12, substeps = 1;
	
	public int id;
	
	
	//constructors
		//circle
	public SoftBody(Vector2 pos, double r, int id) {
		this.nodes = new Node[num];
		this.base_rest_pos = new Vector2[num];
		this.rest_pos = new Vector2[num];
		this.pos = pos;
		
		for (int x = 0; x<num; x++) {
			this.nodes[x] = new Node(this.pos.x + r * Math.cos(x * 2 * Math.PI / num), this.pos.y + r * Math.sin(x * 2 * Math.PI / num), 0.75);
			System.out.println(this.nodes[x]);
			this.rest_pos[x] = new Vector2(this.nodes[x].pos);
			this.base_rest_pos[x] = Vector2.sub(this.rest_pos[x], this.pos);
		}
		
		this.pol = new Polygon(this);
		
		//how to arrange springs?
		
		this.springs = new Spring[4 * this.nodes.length];
		
		for (int x = 0; x<this.nodes.length; x++) {
			this.springs[x] = new Spring(this.nodes[x], this.nodes[(x + 1) % this.nodes.length], k / num);
			this.springs[this.nodes.length + x] = new Spring(this.nodes[x], this.nodes[(x + num / 4) % this.nodes.length], k / num);
			this.springs[2 * this.nodes.length + x] = new Spring(this.nodes[x], this.pos, k / num);
			//this.springs[2 * this.nodes.length + x] = new Spring(this.nodes[x], this.nodes[(x + num / 2 - 1) % this.nodes.length], k / num);
			this.springs[3 * this.nodes.length + x] = new Spring(this.nodes[x], this.rest_pos[x], k / num);
		}
		
		this.id = id;
		
		this.fill = Color.red;//new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
		
		//TODO: id & find_volume
	}
		//shape
	//update (nodes, springs, etc.)
	public void update() {
		long a = System.nanoTime(), total_start = System.nanoTime();
		
		
			this.apply_global_forces();
			
			
		long b = System.nanoTime();
		
		//System.out.println("DT GLOBAL FORCES: " + (b - a) / 1000000.0);
		
		a = System.nanoTime();
			//System.out.println(this.nodes[0]);
			this.apply_spring_forces();
			
			b = System.nanoTime();
			
			//System.out.println("DT Spring forces: " + (b - a) / 1000000.0);
			
			a = System.nanoTime();
			//System.out.println(this.nodes[0]);
			this.integrate_nodes(1.0 / Base.fps);
			
			b = System.nanoTime();
			
			//System.out.println("DT integrate nodes: " + (b - a) / 1000000.0);
			
			a = System.nanoTime();
			//System.out.println(this.nodes[0]);
			//System.out.println(this.nodes[0]);
			
			
			for (int x = 0; x<Base.sbs.length; x++) {
				if (x == this.id) continue;
				this.displace(Base.sbs[x]);
			}
			
			b = System.nanoTime();
			
			//System.out.println("DT Displace SBS: " + (b - a) / 1000000.0);
			
			
			a = System.nanoTime();
			this.displace_ground();
			
			b = System.nanoTime();
			
			//System.out.println("DT Displace GROUND: " + (b - a) / 1000000.0);
			
			a = System.nanoTime();
		
		this.configure();
		
		b = System.nanoTime();
		
		long total_end = System.nanoTime();
		
		//System.out.println("DT Configure: " + (b - a) / 1000000.0);
		//System.out.println("TOTAL SB UPDATE: " + (total_end - total_start) / 1000000.0);
		
		//System.out.println(this.nodes[0]);
		
		//loop 10 times
		//update node gravity forces
		//update springs
		//move nodes
		//
	}
	//apply_global_forces
	public void apply_global_forces() {
		for (int x = 0; x<this.nodes.length; x++) 
			this.nodes[x].force = new Vector2(0, 9.81 * this.nodes[x].mass);
	}
	//apply_spring_forces
	public void apply_spring_forces() {
		for (int x = 0; x<this.springs.length; x++)
			this.springs[x].update();
	}
	public void integrate_nodes(double dt) {
		for (int x = 0; x<this.nodes.length; x++) 
			this.nodes[x].update(dt);
	}
	public void displace_ground() {
		for (int x = 0; x<this.nodes.length; x++)
			if (Base.pol.intersect(this.nodes[x].pos))
				Base.pol.displace(this.pos, this.nodes[x], false);
	}
	//collide:
		//with node
		//with softbody
	//displace:
		//node
		//softbody
	
	//shape memory section
	//find_pos
	public void find_pos() {
		Vector2 out = new Vector2();
		
		for (int x = 0; x<this.nodes.length; x++) out.add(this.nodes[x].pos);
		
		this.pos.set(out.mult(1.0 / this.nodes.length));
	}
	//find_orientation
	public void find_orientation() {
		//find angle to displace by
		this.find_pos();
		
		double angle = 0.0;
		
		for (int x = 0; x<this.nodes.length; x++) {
			Vector2 vec = Vector2.sub(this.nodes[x].pos, this.pos);
			
			angle += Vector2.angle(this.base_rest_pos[x], vec);
		}
		
		//this.angle = (this.id % 2 == 0 ? 1 : -1) * (double)(System.currentTimeMillis() % 5000) / 5000 * Math.PI * 2;

		this.angle = angle / this.nodes.length;
	}
	//change orientation direction of frame
	public void change_orientation() {
		//make sure to use .set since it alters the vector2 without reassigning memory
		for (int x = 0; x<this.rest_pos.length; x++) {
			this.rest_pos[x].set(Vector2.add(this.pos, this.base_rest_pos[x].rotate(this.angle)));
		}
	}
	//change location and rotation of rest_pos
	public void configure() {
		this.find_orientation();
		this.change_orientation();
	}
	
	//to_polygon (maybe make memory-assigned polygon that stays in touch w Nodes)
	//expand volume (only for spheres)
	
	
	//intersect
	
	public boolean intersect(SoftBody in) {
		return this.pol.intersect(in.pol);
	}
	
	public void displace(SoftBody in) {
		
		if (!in.pol.intersect(this.pol)) return;
		//displacing our nodes out from the softbody in
		for (int x = 0; x<this.nodes.length; x++) {
			if (!in.pol.intersect(this.nodes[x].pos)) continue;
			
			int index = 0;
			double pb = 99999999;
			
			Vector2 p = new Vector2();
			
			
			for (int i = 0; i<in.pol.sides.length; i++) {
				Vector2 pnt = Line.find_node_on_line(in.pol.sides[i], this.nodes[x].pos);
				//using sign filter out any points outside the polygon
				//acc irrelevant
				
				/*
				double sign = Math.signum(Vector2.dot(Vector2.sub(in, pnt), this.sides[x].norm()));
				
				if (sign < 0) continue;*/
				
				
				
				//if (Vector2.dot(Vector2.sub(, pnt)))
				
				//if (Vector2.dot(Vector2.sub(pnt, this.nodes[x].pos), Vector2.sub(this.nodes[x].pos, in.pos)) < 0) continue;
				
				double temp = Vector2.dist(this.nodes[x].pos, pnt);

				//double temp = Math.signum(Vector2.dot(in, in)) * Vector2.dist(in, Line.find_node_on_line(this.sides[x], in));
				if (temp < pb) {
					p = pnt;
					index = x;
					pb = temp;
				}
			}
			
			Line line = in.pol.sides[index];
			
			Vector2 n = Vector2.sub(p, this.nodes[x].pos).norm();
			
			double t = Vector2.dist(line.a, p) / Vector2.dist(line.a, line.b);
			
			if (Vector2.dist(line.a, line.b) < 1e-3) t = 0;
			
			double displacement_constant = 2.0 / 3;
			
			double da = (1 - t) * displacement_constant * pb;
			double dc = t * displacement_constant * pb;
			double db = pb - da - t * (dc - da);
			
			final double FDAMP = 1, VDAMP = 0.60;
			
			long time_a = System.nanoTime();
			
			Vector2 ve1 = n.mult(db), ve2 = n.mult(-da), ve3 = n.mult(-dc);
			
			this.nodes[x].force = new Vector2(0, 0);
			in.nodes[index].force = new Vector2(0, 0);
			in.nodes[(index + 1) % in.nodes.length].force = new Vector2(0, 0);
			
			//displace positions
			//this.nodes[x].pos.set(p);
			
			this.nodes[x].pos.set(p);
			//in.nodes[index].pos.add(ve2);//.set(Vector2.add(in.nodes[index].pos, ve2));
			//in.nodes[(index + 1) % in.nodes.length].pos.add(ve3);//.set(Vector2.add(in.nodes[(index + 1) % in.nodes.length].pos, ve3));
			//this.nodes[x].pos.add(ve1);//.set(Vector2.add(this.nodes[x].pos, ve1));
			
			//calculate new velocities
			
			final double ELASTICITY = 0.3; 
			Vector2 V1 = this.nodes[x].vel;

			// Calculate the relative velocity along the normal (V_rel_n = V1_n)
			double vn = Vector2.dot(V1, n);

			// If the penetrating node is still moving INTO the other body (negative velocity):
			if (vn < 0) {
			    // Calculate the impulse magnitude (J) needed to reverse the velocity
			    // J = (-(1 + e) * V_rel_n) / (1/m1 + 1/m2)
			    // Assuming m2 (the segment mass) is large/infinite for simplicity, m2 drops out.
			    // J = -(1 + e) * vn * m1 
			    
			    // Instead of full impulse, just apply damped reflection to the normal component.
			    Vector2 V_normal = n.mult(vn);
			    
			    // V_new_normal = -E * V_old_normal
			    Vector2 V_new_normal = V_normal.mult(-ELASTICITY); 
			    
			    // Total impulse applied to V1: V_new_normal - V_old_normal
			    Vector2 impulse = Vector2.sub(V_new_normal, V_normal);
			    
			    // Apply impulse to the penetrating node's velocity
			    this.nodes[x].vel.add(impulse);

			    // Apply damping to stop excessive sliding/energy transfer
			    this.nodes[x].vel = this.nodes[x].vel.mult(VDAMP);
			    
			    // Since the segment nodes (in.nodes[index], in.nodes[index+1]) are part of another soft body, 
			    // simply damping them slightly is often enough to prevent secondary explosion.
			    in.nodes[index].vel = in.nodes[index].vel.mult(0.9);
			    in.nodes[(index + 1) % in.nodes.length].vel = in.nodes[(index + 1) % in.nodes.length].vel.mult(0.9);
			}
			
			long time_b = System.nanoTime();
			
			//System.out.println("DT COL: " + (time_b - time_a) / 1000000.0);
			
			/*
			Vector2 v1 = this.nodes[x].vel;
			Vector2 v2 = Vector2.add(in.nodes[index].vel, in.nodes[(index + 1) % in.nodes.length].vel).mult(0.5);
			
			double m1 = this.nodes[x].mass;
			double m2 = in.nodes[index].mass + in.nodes[(index + 1) % in.nodes.length].mass;
			
			Vector2 v1f = Vector2.add(v1.mult((m1 - m2) / m1 + m2), v2.mult(2 * m2 / (m1 + m2)));
			Vector2 v2f = Vector2.add(v1.mult(2 * m1 / (m1 + m2)), v2.mult((m2 - m1) / (m1 + m2)));
			
			//extra velocities
			Vector2 ve1 = n.mult(db), ve2 = n.mult(-da), ve3 = n.mult(-dc);
			
			//force and velocity damping
			
			//position, vel, force updates
			this.nodes[x].force = new Vector2(0, 0);
			in.nodes[index].force = new Vector2(0, 0);
			in.nodes[(index + 1) % in.nodes.length].force = new Vector2(0, 0);
			
			//displace positions
			//this.nodes[x].pos.set(p);
			
			in.nodes[index].pos.add(ve2);//.set(Vector2.add(in.nodes[index].pos, ve2));
			in.nodes[(index + 1) % in.nodes.length].pos.add(ve3);//.set(Vector2.add(in.nodes[(index + 1) % in.nodes.length].pos, ve3));
			this.nodes[x].pos.add(ve1);//.set(Vector2.add(this.nodes[x].pos, ve1));
			
			//set velocities
			//this.nodes[x].vel = in.nodes[index].vel = in.nodes[(index + 1) % in.nodes.length].vel = new Vector2();
			
			this.nodes[x].vel = Vector2.add(v1f, ve1).mult(VDAMP);
			in.nodes[index].vel = Vector2.add(v2f, ve2).mult(VDAMP);
			in.nodes[(index + 1) % in.nodes.length].vel = Vector2.add(v2f, ve3).mult(VDAMP);
			*/
			
		}
	}
	
	
	//draw
	public void draw(Graphics g) {
		//if (!(this.id == 2 || this.id == 3 || this.id == 4 || this.id == 6 || this.id == 14)) return;
		
		int[][] temp = this.to_polygon();
		g.setColor(this.fill);		
		g.drawPolygon(temp[0], temp[1], this.nodes.length);
		
		temp = this.to_polygon(this.rest_pos);
		
		g.setColor(Color.red);
		
		//g.drawPolygon(temp[0], temp[1], this.rest_pos.length);
		
		Vector2 tempA = Vector2.add(this.pos, new Vector2(Base.screen_width / 2, Base.screen_height / 2));
		
		g.drawString(this.id + "", (int)tempA.x, (int)tempA.y);
		
		/*for (int x = 0; x<this.nodes.length; x++) {
			Vector2 tempB = Vector2.add(this.nodes[x].pos, new Vector2(Base.screen_width / 2, Base.screen_height / 2));
			
			g.drawString(x + "", (int)tempB.x, (int)tempB.y);
			
			this.nodes[x].draw_node(g);
		}*/
		
		
		//for (int x = 0; x<this.springs.length; x++) this.springs[x].draw(g);
		//for (int x = this.nodes.length * (this.nodes.length - 1) / 2; x<this.springs.length; x++) this.springs[x].draw(g);
	
	}
	
	public int[][] to_polygon() {
		int[][] out = new int[2][this.nodes.length];
		
		for (int x = 0; x<out[0].length; x++) {
			out[0][x] = (int)this.nodes[x].pos.x + Base.screen_width / 2;
			out[1][x] = (int)this.nodes[x].pos.y + Base.screen_height / 2;

		}
		
		return out;
	}
	
	public int[][] to_polygon(Vector2[] in) {
		int[][] out = new int[2][this.nodes.length];
		
		for (int x = 0; x<out[0].length; x++) {
			out[0][x] = (int)in[x].x + Base.screen_width / 2; 
			out[1][x] = (int)in[x].y + Base.screen_height / 2;

		}
		
		return out;
	}
	
	
	
}
