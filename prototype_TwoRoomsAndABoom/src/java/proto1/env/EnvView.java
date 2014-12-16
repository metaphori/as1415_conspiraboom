package proto1.env;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.util.logging.Logger;

import javax.swing.JPanel;

import proto1.game.impl.Player;
import proto1.game.impl.TeamRole;
import proto1.game.impl.TeamRoles;
import sun.font.FontScaler;
import jason.environment.grid.GridWorldView;

public class EnvView extends GridWorldView {

	EnvModel hmodel;
	
	protected Logger logger = Logger.getLogger("View");	
	
	public EnvView(EnvModel model){
		super(model, "Two Rooms and a Boom", 1000);
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
	

	@Override
	public void drawAgent(Graphics g, int x, int y, Color c, int id) {
		int agId = hmodel.getAgAtPos(x, y);
		Player p = hmodel.getPlayerById(id);
		
		if(p.getRole().getTeamRole().equals(TeamRoles.PRESIDENT)){
			c = Color.BLUE;
		} else if(p.getRole().getTeamRole().equals(TeamRoles.BOMBER)){
			c = Color.RED;
		} else{
			c = Color.GRAY;
		}
		
		super.drawAgent(g, x, y, c, -1);
		
		g.setColor(Color.BLACK);
		super.drawString(g, x, y, defaultFont, p.getName());
	}	
	
	
}
