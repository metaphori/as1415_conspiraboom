package proto1.game;

import java.util.List;

//import proto1.env.Game.RoundPhase;

public class Room extends GameConcept {
	
	protected Player leader;
	//protected RoundPhase phase;
	//protected List<Player> players;
	
	public Room(String name){
		super(name);
	}
	
	/*
	public void setPlayers(List<Player> players){
		this.players = players;
	}
	*/
	/*
	public void setPhase(RoundPhase phase){
		this.phase = phase;
	}
	public RoundPhase getRoundPhase(){
		return this.phase;
	}
	*/
	
	public void setLeader(Player leader){
		this.leader = leader;
	}
	
	public Player getLeader(){
		return this.leader;
	}
	
}
