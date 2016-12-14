package swarm.util;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;

public class Trajectory {
	
	BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
	ImageFrame frame = new ImageFrame(image);
	
	Graphics2D g = image.createGraphics();
	
	float scale = 2.0f;
	float mousex, mousey;
	private MouseMotionListener mouser = new MouseAdapter() {
		public void mouseMoved(MouseEvent e) {
			mousex = e.getX() / scale;
			mousey = e.getY() / -scale + 300;
		};
	};
	
	
	private Trajectory() {
		frame.getContentPane().addMouseMotionListener(mouser );
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	List<Projectile> bullets = new ArrayList<Projectile>();
	
	
	
	int timer = 0;
	private String dista = "hehe";
	
	public void run() throws Exception {
		while(true) {
			
			
			g.clearRect(0, 0, image.getWidth(), image.getHeight());
			AffineTransform trans = g.getTransform();
			
			g.translate(0, 600);
			g.scale(scale, -scale);
			
			Iterator<Projectile> iter = bullets.iterator();
			while(iter.hasNext()) {
				Projectile p = iter.next();
				p.update();
				g.drawLine((int)p.lx, (int)p.ly, (int)p.x, (int)p.y);
				
				if(p.dead)
					iter.remove();
			}
			
			g.fillOval((int)mousex-2, (int)mousey-2, 4, 4);
			
			if(++timer > 15) {
				timer = 0;
				Projectile p = new Projectile();
				p.x = p.lx = 10;
				p.y = p.ly = 10;
				
				
				dista = "dist x : " + (mousex - p.x);
				
				Vec3D out = new Vec3D();
				boolean res = Kurwektoria.calculateTrajectory(new Vec3D(mousex - p.x, mousey - p.y, mousex - p.x), 1.5f, 0.01f, true, out);
				if(res) {
					p.setVelocity(out);
					bullets.add(p);
				}
			}
			
			
			g.setTransform(trans);
			g.drawString(dista, 10, 30);
			
			frame.repaint();
			Thread.sleep(1000/120);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new Trajectory().run();
	}

	
	
	class Projectile {
		public boolean dead = false;
		float x,y;
		float vx,vy;
		float lx,ly;
		
		public void update() {
			lx = x;
			ly = y;
			x += vx;
			y += vy;
			
			//vx*= 0.99f;
			//vy*= 0.99f;
			
			vy -= 0.01f;
			
			if(x < 0 || x > 400)
				dead = true;
			else if(y <0 || y > 300)
				dead = true;
		}
		
		public void setHeading(float x, float y, float speed) {
			float f2 = (float) Math.sqrt(x * x + y * y);
			x /= (float)f2;
			y /= (float)f2;
			x *= (float)speed;
			y *= (float)speed;
			this.vx = x;
			this.vy = y;
		}
		
		public void setVelocity(Vec3D vec3d) {
			vx = (float) vec3d.x;
			vy = (float) vec3d.y;
		}
		
		private float gfactor(float dist, float dy) {
			return dist * dist * 0.002f - ( dy < 0 ? (float)Math.sqrt(dy*dist)*1f : 0 );
		}
		
		private vec2 getPos() {
			return new vec2(x, y);
		}
		
		public void setHeadingTowards(float tx, float ty, float speed) {
			vec2 vec = new vec2();
//			float dx = tx - x;
//			float dy = ty - y;
//			float dst = dx ;//(float)Math.sqrt(dx*dx + dy*dy);
//			
//			dista = "dist: " + dst;
//			dy -= gfactor(Math.abs(dx), dy);s
			
			
			
			this.setHeading(vec.x, vec.y, speed);
		}
	}
}
