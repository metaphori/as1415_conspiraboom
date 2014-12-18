package proto1.game.interfaces;

import java.util.Collection;

import proto1.game.impl.Player;

public interface IRoom extends IGameConcept {
	
	IPlayer getLeader();
	void setLeader(IPlayer newLeader);
	
	Collection<IPlayer> getPlayers();

}
