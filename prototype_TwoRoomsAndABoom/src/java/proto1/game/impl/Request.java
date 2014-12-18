package proto1.game.impl;

import proto1.game.interfaces.IPlayer;
import proto1.game.interfaces.IRequest;

public class Request extends GameConcept implements IRequest {

	protected static long REQID = 0;
	
	protected IPlayer from;
	protected IPlayer to;
	protected String what;
	protected boolean answered;
	protected String answer;
	
	public Request(IPlayer from, IPlayer to, String what){
		super("request_"+(++REQID));
		this.from = from;
		this.to = to;
		this.what = what;
		this.answered = false;
		this.answer = null;
	}

	public IPlayer getInitiator() {
		return this.from;
	}

	public IPlayer getRecipient() {
		return this.to;
	}

	public String getSubject() {
		return this.what;
	}

	public boolean answered() {
		return this.answered;
	}

	public String getAnswer() {
		return this.answer;
	}
	
	public void answer(String answer){
		this.answered = true;
		this.answer = answer;
	}
	
}
