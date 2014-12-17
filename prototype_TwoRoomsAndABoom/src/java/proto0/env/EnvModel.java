package proto0.env;

import java.awt.Graphics;
import java.util.*;
import java.util.logging.Logger;

import proto1.game.config.Rooms;
import proto1.game.config.TeamRoles;
import proto1.game.config.Teams;
import proto1.game.impl.Player;
import proto1.game.impl.PlayerRole;
import proto1.game.impl.Room;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class EnvModel extends GridWorldModel {

/* Every agent gets an identification (a integer from 0 to the number of ag - 1). 
 * The relation of this identification with agent's name should be done in the environment class 
 * and is application dependent.
 * Every type of object in the environment is represented by a bit mask: 
 * an agent is 000010; an obstacle is 000100; .... New types of objects should follow this pattern, 
 * for example, GOLD = 8 (001000), ENEMY=16 (010000), ... 
 * A place with two object is represented by the OR between the masks: an agent and a gold is 001010.
 * Limitations:
 * - The number of agents can not change dynamically
 * - Two agents can not share the same place. More generally, two objects with the same "mask" can not share a place.
*/	
	
	protected int houseSize = 0;
	
	protected Logger logger = Logger.getLogger("prototype_TwoRoomsAndABoom."+Env.class.getName());
	
	protected List<String> agents = new ArrayList<String>();
	
	protected EnvModel(int size) {
		super(size, size, 0); // params: width, height, nbAgs
		this.houseSize = size;
		
		int l = size;
        // Perimeter
        this.addWall(0, 0, 	0, l-1);
        this.addWall(l-1, 0, 	l-1, l-1);
        this.addWall(0, 0, 	l-1, 0);
        this.addWall(0, l-1, 	l-1, l-1);
        
        // Rooms' wall
        this.addWall(l/2, 0,    	l/2, l/2-1);
        this.addWall(l/2, l/2+1,   l/2, l-1);
        
	}
	

	public boolean moveAgent(String agentName, String room){
		int agId = getAgentId(agentName);
		
		int room_start = room.equals(Env.ROOM1) ? 2 : (houseSize/2)+2;
		int room_end = room.equals(Env.ROOM1) ? (houseSize/2)-2 : houseSize-2;
		
		Location loc = null;
		while(loc==null || (loc.x < room_start || loc.x > room_end) )
			loc = this.getFreePos(); // todo: do something better
		
		if(this.getAgPos(agId)==null){
			logger.info("Adding "+agentName+"("+agId+") to pos ("+loc.x+","+loc.y+")");
			this.add(agId, loc.x, loc.y);
		} else{
			logger.info("Moving "+agentName+"("+agId+") to pos ("+loc.x+","+loc.y+")");
			this.setAgPos(this.AGENT, loc.x, loc.y);
		}
		
		view.update(loc.x, loc.y);
		return true;
	}
	
	
	public int getAgentId(String agentName){
		if(!agents.contains(agentName)){
			agents.add(agentName);
		}
		return agents.indexOf(agentName);
	}
}
