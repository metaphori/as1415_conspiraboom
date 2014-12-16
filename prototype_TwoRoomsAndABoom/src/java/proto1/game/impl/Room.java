package proto1.game.impl;

import java.util.Collection;
import java.util.List;

import proto1.game.interfaces.IRoom;

//import proto1.env.Game.RoundPhase;

public class Room extends GameConcept implements IRoom {
	
	protected Player leader;
	
	public Room(String name){
		super(name);
	}
	
	public void setLeader(Player leader){
		this.leader = leader;
	}
	
	public Player getLeader(){
		return this.leader;
	}

	public Collection<Player> getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
