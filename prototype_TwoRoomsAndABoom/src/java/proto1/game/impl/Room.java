package proto1.game.impl;

import java.util.Collection;
import java.util.List;

import proto1.game.interfaces.IPlayer;
import proto1.game.interfaces.IRoom;

//import proto1.env.Game.RoundPhase;

public class Room extends GameConcept implements IRoom {
	
	protected IPlayer leader;
	
	public Room(String name){
		super(name);
	}
	
	public void setLeader(IPlayer leader){
		this.leader = leader;
	}
	
	public IPlayer getLeader(){
		return this.leader;
	}

	public Collection<IPlayer> getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
