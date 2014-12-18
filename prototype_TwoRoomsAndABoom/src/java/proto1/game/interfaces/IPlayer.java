package proto1.game.interfaces;

public interface IPlayer extends IGameConcept {

	void setRole(IPlayerRole role);
	IPlayerRole getRole();
	
	void setRoom(IRoom room);
	IRoom getRoom();
	
}
