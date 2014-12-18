package proto1.game.interfaces;

import java.util.Collection;
import java.util.Iterator;

import proto1.game.impl.Player;

public interface ITurn<T> extends Iterator<T>, IGameConcept {

	T currentTurn();
	
}
