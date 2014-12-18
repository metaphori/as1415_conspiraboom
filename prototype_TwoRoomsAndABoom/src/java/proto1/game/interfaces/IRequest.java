package proto1.game.interfaces;

public interface IRequest extends IGameConcept {

	public IPlayer getInitiator();
	
	public IPlayer getRecipient();
	
	public String getSubject();
	
	public boolean answered();
	public String getAnswer();
	public void answer(String ans);
	
}
