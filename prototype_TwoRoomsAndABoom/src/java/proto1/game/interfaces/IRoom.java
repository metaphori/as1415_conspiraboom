package proto1.game.interfaces;

import java.util.Collection;

import proto1.game.impl.Player;

public interface IRoom {
	
	Player getLeader();
	void setLeader(Player newLeader);
	
	Collection<Player> getPlayers();

}
