package proto0.env;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import sun.font.FontScaler;
import jason.environment.grid.GridWorldView;

public class EnvView extends GridWorldView {

	EnvModel hmodel;
	
	public EnvView(EnvModel model){
		super(model, "Domestic Robot", 700);
		hmodel = model;
		defaultFont = new Font("Arial", Font.BOLD, 16); // change default font					
		
		setVisible(true);		
		
		repaint();		
	}
	
	@Override public void draw(Graphics g, int x, int y, int object){
		System.out.println("draw");
		if(object==Env.DOOR){
			g.setColor(Color.LIGHT_GRAY);
			super.drawString(g, x, y, new Font("Arial", Font.BOLD, 16), "DOOR");
		} 
		else {
			g.setColor(Color.BLACK);
			super.draw(g, x, y, object);
		} 				
	}
	
}
