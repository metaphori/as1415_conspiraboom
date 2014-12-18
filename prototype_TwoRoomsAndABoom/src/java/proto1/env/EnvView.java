package proto1.env;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JPanel;

import proto1.game.config.RoomRoles;
import proto1.game.config.TeamRoles;
import proto1.game.config.Teams;
import proto1.game.impl.Player;
import proto1.game.impl.RoomRole;
import proto1.game.impl.Team;
import proto1.game.impl.TeamRole;
import proto1.game.interfaces.IPlayer;
import sun.font.FontScaler;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.infra.jade.JadeEnvironment;

public class EnvView extends GridWorldView {

	EnvModel hmodel;
	Env env;
	
	protected Logger logger = Logger.getLogger("View");	
	
	public EnvView(EnvModel model, Env env){
		super(model, "Two Rooms and a Boom", 500);
		this.hmodel = model;
		this.env = env;
		defaultFont = new Font("Arial", Font.BOLD, 16); // change default font					
		
		setVisible(true);		
		
		repaint();		
	}
	
	@Override public void draw(Graphics g, int x, int y, int object){
		//System.out.println("draw");
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
		IPlayer reference = hmodel.getPlayerById(0);
		
		super.drawString(g, 0,0, defaultFont, reference.getName());
		
		int agId = hmodel.getAgAtPos(x, y);
		IPlayer p = hmodel.getPlayerById(id);
		
		Team team = null;
		TeamRole tr = TeamRoles.NORMAL;
		RoomRole rr = RoomRoles.NORMAL;
		
		List<Literal> percepts = null;
		//percepts = env.getPercepts(reference.getName());
		percepts = env.consultPercepts(reference.getName());
		
		if(percepts!=null){
		for(Literal lt : percepts){
			if(lt instanceof Structure){
				Structure st = (Structure)lt;
				if(st.getFunctor().equals("role")){
					String name = st.getTerm(0).toString();
					String team_name = st.getTerm(1).toString();
					String role = st.getTerm(2).toString();
					if(name.equals(p.getName())){
						team = team_name.equals(Teams.BLUES.getName()) ? Teams.BLUES : Teams.REDS;
						if(role.equals(TeamRoles.BOMBER))
							tr = TeamRoles.BOMBER;
						else if(role.equals(TeamRoles.PRESIDENT))
							tr = TeamRoles.PRESIDENT;
					}
				} else if(st.getFunctor().equals("leader")){
					String name = st.getTerm(0).toString();
					if(name.equals(p.getName()))
						rr = RoomRoles.LEADER;
				}
			}
		}
		}
		
		if(tr.equals(TeamRoles.PRESIDENT)){
			c = Color.CYAN;
		} else if(tr.equals(TeamRoles.BOMBER)){
			c = Color.ORANGE;
		} else if(team!=null && team.equals(Teams.BLUES)){
			c = Color.BLUE;
		} else if(team!=null && team.equals(Teams.REDS)){
			c = Color.RED;
		} else{
			c = Color.GRAY;
		}
		
		super.drawAgent(g, x, y, c, -1);
		
		g.setColor(Color.BLACK);
		String str = p.getName();
		if(rr == RoomRoles.LEADER)
			str = "*"+str+"*";
		super.drawString(g, x, y, defaultFont, str);
	}	
	
	
}
