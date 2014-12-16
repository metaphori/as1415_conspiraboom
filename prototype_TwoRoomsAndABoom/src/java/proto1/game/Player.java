package proto1.game;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameConcept {

	protected TeamRole role;
	protected Room room;
	public final int AGENT_ID = GetNextAgentId();
	
	public Player(String agentName){
		super(agentName);
		//internal registry: //Player.players.add(this);
	}
	
	public void setRole(TeamRole role){
		this.role = role;
	}
	public TeamRole getTeamRole(){ return this.role; }
	
	public void setRoom(Room room){
		this.room = room;
	}
	public Room getRoom(){ return this.room; }
	
	
	private static int ID_COUNTER = 0;
	public synchronized static int GetNextAgentId(){
		return ID_COUNTER++;
	}
	
	/*
	public static List<Player> players = new ArrayList<Player>();
	public static Player GetAgentWithId(int id){
		return players.get(id);
	}
	*/
}
