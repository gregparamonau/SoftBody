package SoftBody2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Base {
	//create spftbpdy class
	//create polygon class
	//create node class
	//create vector2 class (custom & universal)
	//create Spring class
	//create Line class (for collisions)
	
	public static int screen_width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static int screen_height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public static JFrame frame = null;
	public static JPanel pane = null;
	public static Graphics2D g = null;
	
	public static BufferedImage[] imgs = new BufferedImage[2];
	public static Graphics2D[] gs = new Graphics2D[2];
	
	static int count = 0, fps = 60;
	
	static SoftBody[] sbs = new SoftBody[4];
	
	static Polygon pol = new Polygon(new Vector2[] {
			new Vector2(-600, 0), 
			new Vector2(-450, 0),
			new Vector2(-300, 200),
			new Vector2(300, 200),
			new Vector2(450, 0),
			new Vector2(600, 0),
			new Vector2(600, 550),
			new Vector2(-600, 550)});
	
	public static void main(String [] args) {
		init();
		
		int width = 6, height = 6, radius = 50;
		sbs = new SoftBody[width * height];
		
		for (int x = 0; x<sbs.length; x++) {
			
			//sbs[x] = new SoftBody(new Vector2(), radius, 0);
			sbs[x] = new SoftBody(new Vector2(-200 + x % width * 400 / width, -50 - 2 * radius * (x / width) * 1.1), radius, x);
		}
		
		do {
			long a = System.currentTimeMillis();
			
			long start = System.nanoTime();
			
			for (int x = 0; x<sbs.length; x++) sbs[x].update();
			
			pane.repaint();
			
			draw_view(gs[count % 2]);
			
			long end = System.nanoTime();
			
			long b = System.currentTimeMillis();
			
			System.out.println("DT: " + (end - start) / 1000000.0);
			
			try {Thread.sleep(1000 / fps - (b - a));}catch(Exception e) {}
			
		}while(true);
		
	}
	
	//TODO: make draw method
	
	public static void draw_view(Graphics g) {
		long a = System.nanoTime();
		g.setColor(Color.black);
		g.fillRect(0, 0, screen_width, screen_height);
		
		pol.draw(g);
		
		for (int x = 0; x<sbs.length; x++) sbs[x].draw(g);
		
		long b = System.nanoTime();
		
		//System.out.println("DT DRAW: " + (double)(b - a) / 1000000);
	}
	
	public static void init() {
		frame = new JFrame();
		frame.setSize(screen_width, screen_height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		pane = new JPanel() {
			 @Override
	         protected void paintComponent(Graphics g) {
	        	 super.paintComponent(g);
	        	 //Start.g.drawImage(Start.op.filter(Start.imgs[(count+1)%2], null), 0, 0, Start.pane);
	        	 
	        	 g.drawImage(imgs[count % 2], 0, 0, this);
	         }
		};
		pane.setSize(frame.getWidth(), frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);
		frame.add(pane);
		
		System.out.println("f: " + frame.getWidth() + " " + frame.getHeight());
		System.out.println("p: " + pane.getWidth() + " " + pane.getHeight());
		
		try {Thread.sleep(2000);}catch(Exception e) {e.printStackTrace();}
		g = (Graphics2D) pane.getGraphics();
		
		imgs[0] = new BufferedImage(screen_width, screen_height, BufferedImage.TYPE_INT_ARGB);
		imgs[1] = new BufferedImage(screen_width, screen_height, BufferedImage.TYPE_INT_ARGB);
		
		gs[0] = (Graphics2D) imgs[0].getGraphics();
		gs[1] = (Graphics2D) imgs[1].getGraphics();

	}
}
