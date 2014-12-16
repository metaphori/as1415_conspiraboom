package proto1.game.interfaces;

import java.util.Collection;

public interface ITeam extends IGameConcept {

	Collection<IPlayer> getPlayers();
	
	void addPlayer(IPlayer p);
	void removePlayer(IPlayer p);
	
}
