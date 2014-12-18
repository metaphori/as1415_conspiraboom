package proto1.game.impl;

import java.util.*;

import proto1.game.interfaces.IPlayer;
import proto1.game.interfaces.ITurn;

public class Turn<T> extends GameConcept implements ITurn<T> {
	protected List<T> players = null;
	protected int rounds = 1;
	protected int round = 0;
	protected int k = 0;
	
	protected static int TURN_K = 0;

	protected int nplayers = 0;
	
	public Turn(Collection<T> players, int rounds){
		super("turn_"+(TURN_K++));
		this.players = new ArrayList<T>(players);
		this.nplayers = players.size();
		this.rounds = rounds;
		this.k = 0;
		this.round = 0;
	}
	
	public boolean hasNext() {
		return hasNextInThisRound() || hasNextRound();
	}
	public boolean hasNextInThisRound(){
		return this.k < nplayers;
	}
	
	public boolean hasNextRound(){
		return (round) < rounds;
	}

	public T next() {
		if(hasNextInThisRound()){
			T p = players.get(k++);
			return p;
		} else if(hasNextRound()){
			round++;
			this.k=0;
			T p = players.get(this.k++);
			return p;
		}
		return null;
	}

	public void remove() {
	}

	public T currentTurn() {
		return players.get(this.k-1);
	}
	
	@Override
	public String toString(){
		return this.getName() + "[" + this.k + "/" + players.size() + ", round=" + round + "/"+rounds+"]";
	}
}
