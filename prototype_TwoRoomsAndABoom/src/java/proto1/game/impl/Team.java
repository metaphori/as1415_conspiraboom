package proto1.game.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import proto1.game.interfaces.IPlayer;
import proto1.game.interfaces.ITeam;

public class Team extends GameConcept implements ITeam {

	protected List<IPlayer> players = new ArrayList<IPlayer>();
	
	public Team(String name){
		super(name);
	}

	public Collection<IPlayer> getPlayers() {
		return players;
	}

	public void addPlayer(IPlayer p) {
		players.add(p);
	}

	public void removePlayer(IPlayer p) {
		players.remove(p);
	}
	
}
