package swarm.util;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;


public class ImageFrame extends JFrame {
	
	int scale = 1;
	
	public BufferedImage img;
	
	public ImageFrame(BufferedImage image) {
		this.img = image;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(new JPanel() {
			@Override
			public void paint(Graphics g) {
				if(img!=null)
					g.drawImage(img, 0, 0, img.getWidth()*scale, img.getHeight()*scale, null);
			}
		});
	}
	
	 @Override
	public Dimension getPreferredSize() {
		 if(img != null) {
			 return new Dimension(img.getWidth()*scale, img.getHeight()*scale);
		 }
		 return super.getPreferredSize();
	 }
	

}
