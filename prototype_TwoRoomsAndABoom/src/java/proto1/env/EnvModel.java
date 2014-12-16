package proto1.env;

import java.awt.Font;
import java.awt.Graphics;
import java.util.*;
import java.util.logging.Logger;

import proto1.env.Game.NEW_LEADER;
import proto1.env.Game.NotifyEvents;
import proto1.env.Game.ROOM_PLACEMENT;
import proto1.game.Player;
import proto1.game.Roles;
import proto1.game.Room;
import proto1.game.Rooms;
import proto1.game.Team;
import proto1.game.TeamRole;
import proto1.game.Teams;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class EnvModel extends GridWorldModel implements Observer {

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
	
	protected int num_players = 0;
	protected int size = 10; // the size of the grid
	
	protected Logger logger = Logger.getLogger("Model");
	
	protected List<Player> players = new ArrayList<Player>();
	
	protected Game game;
	
	public class Area {
		public Location topLeft = null;
		public Location bottomRight = null;
		public Area(Location topLeft, Location bottomRight){
			this.topLeft = topLeft;
			this.bottomRight = bottomRight;
		}
	}
	protected Hashtable<Room, Area> rooms = new Hashtable<Room, EnvModel.Area>();
	protected Location doorPosition;
	
	protected EnvModel(Game game, int size) {
		super(size, size, game.num_players); // params: width, height, nbAgs
		this.num_players = game.num_players;
		this.game = game;
		game.addObserver(this);
		
		this.size = size;
		int l = size;
		
		Area room1Area = new Area(new Location(0,0), new Location(l/2-1, l-1));
		Area room2Area = new Area(new Location(l/2+1,0), new Location(l-1,l-1));
		addRoom(Rooms.ROOM1, room1Area);
		addRoom(Rooms.ROOM2, room2Area);
		
		/*
        // Perimeter
        this.addWall(0, 0, 	0, l-1);
        this.addWall(l-1, 0, 	l-1, l-1);
        this.addWall(0, 0, 	l-1, 0);
        this.addWall(0, l-1, 	l-1, l-1);
        
        // Rooms' wall
        this.addWall(l/2, 0,    	l/2, l/2-1);
        this.addWall(l/2, l/2+1,   l/2, l-1);
        */
		
        
        doorPosition = new Location(l/2, l/2);
        this.addDoor(doorPosition.x, doorPosition.y);
	}
	
	protected void addRoom(Room room, Area area){
		rooms.put(room, area);
		drawArea(area);
	}
	
	protected void drawArea(Area area){
		Location tl = area.topLeft;
		Location br = area.bottomRight;
		
		/*   
		 *    ________b_______
		 *   |                |
		 * a |                | c
		 *   |________d_______|
		 */
		
		this.addWall(tl.x, tl.y, tl.x, br.y); // a
		this.addWall(tl.x, tl.y, br.x, tl.y); // b
		this.addWall(br.x, tl.y, br.x, br.y); // c
		this.addWall(tl.x, br.y, br.x, br.y); // d
	}

	protected Location getFreeLocationWithin(int xMin, int xMax, int yMin, int yMax){
		int i=xMin;
		int k=yMin;				
		/*
		// DOESN'T WORK I DON'T KNOW WHY
		for(; i<xMax; i++){
			for(; k<yMax; k++){
				if(this.isFree(this.AGENT, i, k)) break;
			}
		}
		*/
		Location loc = null;
		while(loc==null || (loc.x<xMin || loc.x>xMax || loc.y<yMin || loc.y>yMax))
			loc = this.getFreePos();
		return loc;
	}
	
	protected void addDoor(int x, int y){
		Location doorLocation = new Location(x,y);
		this.add(CLEAN, doorLocation);
		this.add(Env.DOOR, doorLocation);
	}

	public void update(Observable o, Object arg) {
		Game g = (Game)o;
		Game.NotifyEvents event = (Game.NotifyEvents)arg;
		if(event instanceof Game.ROOM_PLACEMENT){
			logger.info("Update from game: ROOM PLACEMENT");
			for(Player p : g.players){
				Room room = p.getRoom();
				int agId = getAgentId(p);
				
				Location loc = getFreePositionInRoom(room);
				
				if(this.getAgPos(agId)==null){
					logger.info("Adding "+p.getName()+"("+agId+") to pos ("+loc.x+","+loc.y+")");
					//this.add(this.AGENT, loc.x, loc.y);
					this.setAgPos(agId, loc.x, loc.y);
				} else{
					logger.info("Moving "+p.getName()+"("+agId+") to pos ("+loc.x+","+loc.y+")");
					this.setAgPos(agId, loc.x, loc.y);
				}
				
				view.update(loc.x, loc.y);
			} // end for
		} // end ROOM_PLACEMENT
		else if(event instanceof Game.NEW_LEADER){
			logger.info("Update from game: NEW LEADER");
			Player leader1 = Rooms.ROOM1.getLeader();
			Player leader2 = Rooms.ROOM2.getLeader();
			Player[] leaders = new Player[]{leader1, leader2};
			for(int i=0; i<leaders.length; i++ ){
				if(leaders[i]!=null){
					int agId = getAgentId(leaders[i]);
					Location loc = getAgPos(agId);
					view.drawString(view.getGraphics(), loc.x, loc.y, new Font(Font.SERIF,Font.BOLD,18), 
							"Leader");
					view.update();
				}
			}
		} else if(event instanceof Game.HOSTAGES_EXCHANGE){
			logger.info("Update from game: HOSTAGES EXCHANGE");
			Game.HOSTAGES_EXCHANGE actualEv = (Game.HOSTAGES_EXCHANGE)event;
			Player h1 = actualEv.h1;
			Player h2 = actualEv.h2;
			moveHostage(h1);
			moveHostage(h2);
		}
	}
	
	public void moveHostage(Player p){
		/* Player p has changed room.
		 * This has to reflect in the model.
		 * Let's do it step by step.
		 */
		int agentId = getAgentId(p);
		Location currentPos = getAgPos(agentId);
		Location newPos = getFreePositionInRoom(p.getRoom());
		moveAgentFromTo(agentId, currentPos, doorPosition);
		moveAgentFromTo(agentId, doorPosition, newPos);
	}
	
	public enum Direction { LEFT, RIGHT, TOP, DOWN };
	public void moveAgentFromTo(int agId, Location from, Location to){
		int x = from.x; int y = from.y;
		int new_x = to.x; int new_y = to.y;
		while(!(x==new_x && y==new_y)){
			Direction D = x < new_x ? Direction.LEFT : Direction.RIGHT;
			if(x == new_x)
				D = y < new_y ? Direction.DOWN : Direction.TOP;
			
			switch(D){
				case LEFT: x++; break;
				case RIGHT: x--; break;
				case TOP: y--; break; // note: inverted (origin is "above")
				case DOWN: y++; break;
			}
			this.setAgPos(agId, x, y);
			try{
				Thread.sleep(200);
			} catch(Exception exc){}
		}
	}
	
	public Location getFreePositionInRoom(Room room){
		int room_start = room.equals(Rooms.ROOM1) ? 2 : (size/2)+2;
		int room_end = room.equals(Rooms.ROOM1) ? (size/2)-2 : size-2;
		
		/* factor OUT THE FOLLOWEING */
		Location loc = null;
		while(loc==null || (loc.x < room_start || loc.x > room_end) )
			loc = this.getFreePos(); // todo: do something better
		return loc;
	}
	
	
	
	/*
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
	*/
	
	public int getAgentId(Player player){
		if(!players.contains(player)){
			players.add(player);
		}
		return players.indexOf(player);
	}
	
	public Player getPlayerById(int id){
		for(int i=0; i<players.size(); i++){
			if(id==i) return players.get(i);
		}
		return null;
	}
	
	public void setWinner(Team team){
		view.drawString(view.getGraphics(), size/2, size/2,
				new Font(Font.SERIF, Font.BOLD, 30),
				"WINNER => " + team.getName());
		view.update(view.getGraphics());
		view.repaint();
	}
	
}
