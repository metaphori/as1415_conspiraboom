package proto1.game.impl;

import java.util.ArrayList;
import java.util.List;
import proto1.game.interfaces.*;

public class Player extends GameConcept implements IPlayer {

	protected IPlayerRole role;
	protected IRoom room;
	public final int AGENT_ID = GetNextAgentId();
	
	public Player(String agentName){
		super(agentName);
		//internal registry: //Player.players.add(this);
	}
	
	public void setRole(IPlayerRole role){
		this.role = role;
	}
	public IPlayerRole getRole(){ return this.role; }
	
	public void setRoom(IRoom room){
		this.room = room;
	}
	public IRoom getRoom(){ return this.room; }
	
	
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
