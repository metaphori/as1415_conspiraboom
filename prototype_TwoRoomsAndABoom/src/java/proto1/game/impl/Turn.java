package proto1.game.impl;

import java.util.*;

import proto1.game.interfaces.ITurn;

public class Turn extends GameConcept implements ITurn {
	protected List<Player> players = null;
	protected int rounds = 1;
	protected int round = 0;
	protected int k = 0;
	
	protected static int TURN_K = 0;
	
	public Turn(Collection<Player> players, int rounds){
		super("turn"+(TURN_K++));
		this.players = new ArrayList<Player>(players);
		this.rounds = rounds;
	}
	
	public boolean hasNext() {
		return hasNextInThisRound() || hasNextRound();
	}
	public boolean hasNextInThisRound(){
		return (k+1) < players.size();
	}
	
	public boolean hasNextRound(){
		return (round+1) < rounds;
	}

	public Player next() {
		if(hasNextInThisRound()){
			k++;
			Player p = players.get(k);
			return p;
		} else if(hasNextRound()){
			k=0;
			Player p = players.get(0);
			return p;
		}
		return null;
	}

	public void remove() {
	}

	public Player currentTurn() {
		return players.get(k);
	}
	
	@Override
	public String toString(){
		return this.getName() + "[" + k + "/" + players.size() + ", round=" + round + "/"+rounds+"]";
	}
}
