// Environment code for project prototype_TwoRoomsAndABoom
package proto1.env;
import jason.asSyntax.*;
import jason.environment.*;

import java.awt.Font;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

import proto1.env.Game.GamePhase;
import proto1.env.Game.NEXT_TURN;
import proto1.game.config.Actions;
import proto1.game.config.Rooms;
import proto1.game.impl.Player;
import proto1.game.impl.Request;
import proto1.game.impl.Room;
import proto1.game.impl.Team;
import proto1.game.impl.Turn;
import proto1.game.interfaces.IPlayer;
import proto1.game.interfaces.IRequest;
import proto1.game.interfaces.IRoom;
import proto1.game.interfaces.ITurn;
import proto1.gui.UserCommandFrame;
import utils.Tuple;
import utils.Utils;

public class Env extends Environment implements Observer {

	/* Every type of object in the env is represented by a bit mask: an agent is 000010; an obstacle is 000100; */
	public static final int PLAYER = 16;
	public static final int LEADER = 32;
	public static final int DOOR = 64;	
	
    private Logger logger = Logger.getLogger("prototype_TwoRoomsAndABoom."+Env.class.getName());

    protected EnvModel model;
    protected EnvView view;
    protected Game game;
    
    public final static int DEFAULT_NUM_PLAYERS = 10;
    public final static int SIZE = 21;
    

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        //addPercept(Literal.parseLiteral("percept(demo)"));
        
        int num_players = DEFAULT_NUM_PLAYERS;
        if(args.length>=2){
        	num_players = Integer.parseInt(args[1]);
        }
        
        this.game = new Game(num_players);
        this.game.addObserver(this);
        
        model = new EnvModel(this.game, SIZE);
        if(args.length>=1 && args[0].equals("gui")){
        	view = new EnvView(model, this);
        	model.setView(view);
        }
    }

    IRequest lastRequest = null;
    @Override
    public synchronized boolean executeAction(String agName, Structure action) {
    	try{
	    	boolean result = false;
	
	    	Literal act = action;
	    	
	    	List<Term> terms = act.getTerms();
	    	String functor = act.getFunctor();
	    	IPlayer p = game.getPlayerFromName(agName);
	    	IRoom room = null;
	    	if(p!=null)
	    		room = p.getRoom();
	    	
	    	if(functor.equals(Actions.DELEGATE_TO_HUMAN)){
	    		String str = UserCommandFrame.AskCommandToUser("Player: " + p.getName() + "; " + p.getRole() + " - Phase: " + game.state);
	    		act = Structure.parseLiteral(str);
	    	} 
	    	
	    	terms = act.getTerms();
	    	functor = act.getFunctor();    	
	    	
	    	if(functor.equals(Actions.WANNA_PLAY) && game.IsAt(GamePhase.Init) ){
	    		logger.info(">>> " + agName + " wants to play." );
	    		result = game.AddPlayer(agName);
	    	} else if(functor.equals(Actions.VOTE_LEADER) && game.IsAt(GamePhase.LeaderSelection)){
	    		String candidateLeader = ((Atom)terms.get(0)).toString();
	    		logger.info(">>> " + p + " has voted " + candidateLeader);
	    		result = game.Vote(agName, candidateLeader);
	    	} else if(functor.equals(Actions.SELECT_HOSTAGE) && game.IsAt(GamePhase.HostageSelection)){
	    		String hostage = ((Atom)terms.get(0)).toString();
	    		logger.info(">>> " + p + " has selected hostage " + hostage);    		
	    		result = game.SelectHostage(p, hostage);
	    	} else if(functor.equals(Actions.OK_I_AM_DONE) && game.IsAt(GamePhase.Interaction)){   		
	    		IPlayer playerOfTurn = game.currentTurn();
	    		logger.info(">>> " + p + " (="+playerOfTurn+") has completed its turn");    		
	    		result = game.advanceTurn();
	    		return result;
	    	} else if(functor.equals(Actions.CO_REVEAL) && game.IsAt(GamePhase.Interaction)){  
	    		String dest = ((Atom)terms.get(0)).toString();
	    		logger.info(">>> " + p + " wants to co-reveal with " + dest);
	    		
			    	lastRequest = sendRequest(p, functor, game.getPlayerFromName(dest));
			    		
			    	if(lastRequest==null) return false;
			    		
			    	result = true;	    			
		    		
			    	logger.info("Waiting for response");
		    		while(result && !lastRequest.answered())
		    			this.wait();
		    		logger.info("Sblocked now from waiting [" + agName);
		    		return true;
	    		
	    	} else if(functor.equals(Actions.ACCEPT) && game.IsAt(GamePhase.Interaction) && lastRequest!=null ){
	    		logger.info(">>> " + p + " accepts request");
	    		
	    			result = acceptRequest(p, lastRequest);
	    			this.notify();
	    			logger.info("Sblocking request");
	    			return result;

	    	} else if(functor.equals(Actions.REJECT) && game.IsAt(GamePhase.Interaction) && lastRequest!=null ){
	    		logger.info(">>> " + p + " rejects request");
	    		
	    			result = rejectRequest(p, lastRequest);
	    			this.notify();
	    			logger.info("Sblocking request");
	    			return result;
	    	} else{
	    		logger.severe("°°°°°°°°°°°°°°°°°°°°°°°°°° action not recognized from " + p + ": "+ functor);
	    	}
	
			return result;
    	} catch(Exception exc){
    		logger.severe("Exception: " + exc.getMessage());
    		exc.printStackTrace();
    		return false;
    	}
    }  
    
    public synchronized IRequest sendRequest(IPlayer from, String what, IPlayer to){
    	IRequest request = new Request(from, to, what);
    	
    	Literal literal = Literal.parseLiteral("request("+request.getName()+",co_reveal," 
    			+ from.getName() + ")");
    	addPercept(to.getName(), literal);
    	
    	return request;
    }
    public synchronized boolean acceptRequest(IPlayer me, IRequest req){
    	if(!req.getRecipient().equals(me)){
    		logger.severe("ERROR: I'm responding to a request that wasn't for me. It was for " +req.getRecipient());
    		return false; // Request 'req' is not for me
    	}
    	logger.info("//////////////////////\\\\\\\\\\\\\\\\\\\\\\\\");
    	req.answer(Actions.ACCEPT);
    	
    	IPlayer initiator = req.getInitiator();
    	Literal initiatorIdentity = Literal.parseLiteral("role("+initiator.getName()+","+
    			initiator.getRole().getTeam().getName()+","
				+initiator.getRole().getTeamRole().getName()+")");
    	Literal myIdentity = Literal.parseLiteral("role("+me.getName()+","+
    			me.getRole().getTeam().getName()+","
				+me.getRole().getTeamRole().getName()+")");
    	
    	addPercept(me.getName(), initiatorIdentity);
    	addPercept(initiator.getName(), myIdentity);
    	
    	return true;
    }
    public synchronized boolean rejectRequest(IPlayer me, IRequest req){
    	if(!req.getRecipient().equals(me)){ 
    		logger.severe("ERROR: I'm rejecting a request that wasn't for me. It was for " +req.getRecipient());
    		return false; // Request 'req' is not for me
    	}
    	
    	req.answer(Actions.REJECT);
    	
    	String context = "context(round("+game.round+"))";
    	Literal literal = Literal.parseLiteral("reject("+me.getName()+","+
    	req.getInitiator().getName()+","+req.getSubject()+","+context+")");
    	AddPerceptInRoom(me.getRoom(), literal);
    	
    	return true;
    }

	public synchronized void update(Observable obj, Object arg) {
		Game game = (Game)obj;
		Game.NotifyEvents event = (Game.NotifyEvents)arg;
		if(event instanceof Game.GAME_PHASE_COMPLETED){
			Game.GAME_PHASE_COMPLETED ev = (Game.GAME_PHASE_COMPLETED)event;
			GamePhase completed_phase = ev.phase;
			
			switch(completed_phase){
				/*********************************************************/
				/** INIT COMPLETED **/
				/*********************************************************/
				case Init:
					clearAllPercepts();
					logger.info("COMPLETED: INIT");
					game.SetupNewRound();
					break;
					
				/*********************************************************/
				/** ROUND SETUP COMPLETED **/
				/*********************************************************/					
				case SetupRound:
					logger.info("COMPLETED: ROUND SETUP" + game.round);

					removePerceptsByUnif(Literal.parseLiteral("room(_,_)[source(_)]"));					
					removePerceptsByUnif(Literal.parseLiteral("round(_)[source(_)]"));	
					addPercept(Literal.parseLiteral("round("+game.round+")"));
					
					for(IPlayer p : game.players){
						removePerceptsByUnif(p.getName(), Literal.parseLiteral("my_role(_,_)[source(_)]"));
						removePerceptsByUnif(p.getName(), Literal.parseLiteral("my_room(_)[source(_)]"));
						// NOTE: room_leader is not global as it is room specific
						removePerceptsByUnif(p.getName(), Literal.parseLiteral("room_leader(_)[source(_)]"));
						removePerceptsByUnif(p.getName(), Literal.parseLiteral("turn(_)[source(_)]"));
						
						// common percept
						addPercept(Literal.parseLiteral("room("+p.getName()+"," +p.getRoom().getName()+")" ));
						
						// agent-specific percepts
		    			addPercept(p.getName(), Literal.parseLiteral("my_room("+p.getRoom().getName()+")"));
		    			addPercept(p.getName(), 
		    					Literal.parseLiteral("my_role("+p.getRole().getTeam().getName()+","
		    							+p.getRole().getTeamRole().getName()+")"));
		    		}   
					
		    		// Give a start to players!!!
		    		removePerceptsByUnif(Literal.parseLiteral("phase(_)[source(_)]"));
		    		addPercept(Literal.parseLiteral("phase(leader_selection)"));		    		
					break;
					
				/*********************************************************/
				/** LEADER SELECTION PERFORMED **/
				/*********************************************************/
				case LeaderSelection:
					logger.info("COMPLETED: LEADER SELECTION");

					// communicate leader to all room's players
		    		for(IRoom room : Rooms.asList()){
		    			IPlayer leader = room.getLeader();
		    			logger.info("Leader of " + room + " is " + leader);
		    			Literal literal = Literal.parseLiteral("room_leader("+leader.getName()+")");
		    			AddPerceptInRoom(room, literal);					
		    		}
		    		
		    		// Give a start to players!
		    		removePerceptsByUnif(Literal.parseLiteral("phase(_)[source(_)]"));
		    		addPercept(Literal.parseLiteral("phase(interaction)"));		 
					
		    		game.ProceedWithInteraction(); 
		    		//new Timer().schedule(new EndInteractionTask(game), getInteractionTime(game.round));		    		
		    		
		    		break;
		    		
				/*********************************************************/
				/** INTERACTION PERFORMED **/
				/*********************************************************/
				case Interaction:
					logger.info("COMPLETED: INTERACTION");
					removePerceptsByUnif(Literal.parseLiteral("phase(_)"));
		    		addPercept(Literal.parseLiteral("phase(hostages_exchange)"));   					
					break;

				/*********************************************************/
				/** HOSTAGE SELECTION COMPLETED **/
				/*********************************************************/				
				case HostageSelection:
					logger.info("COMPLETED: HOSTAGE SELECTION");
					game.SetupNewRound();		
					break;
					
				/*********************************************************/
				/** END **/
				/*********************************************************/					
				case End: /* do nothing */
					logger.info("******************************************************************\n");
					logger.info("************************** END OF GAME ***************************");
					logger.info("******************************************************************");
					Team winner = game.getWinner();
					if(winner==null){
						logger.info("NO WINNER");
					}else{
						logger.info("THE WINNER IS..................." + winner);
						model.setWinner(winner);
					}
					break;
			} // end switch completed phase
		} // end if(completed event)
		else if(event instanceof NEXT_TURN){
			NEXT_TURN turnev = (NEXT_TURN)event;
    		IPlayer player = turnev.player;
    		IRoom room = player.getRoom();
    		
    		Literal removeliteral = Literal.parseLiteral("turn(_)[source(_)]");
    		RemovePerceptInRoom(room, removeliteral);
    		
    		Literal literal = Literal.parseLiteral("turn("+player.getName()+")");
		    AddPerceptInRoom(room, literal);
		}
	} // end update() method
	
	public synchronized void AddPerceptInRoom(IRoom room, Literal literal){
		for(IPlayer p : game.getPlayersInRoom(room)){
			addPercept(p.getName(), literal);
		}		
	}
	
	public synchronized void RemovePerceptInRoom(IRoom room, Literal literal){
		for(IPlayer p : game.getPlayersInRoom(room)){
			removePerceptsByUnif(p.getName(), literal);
		}
	}
	
	public int getInteractionTime(int round){
		return 1000;
	}
    
    public class EndInteractionTask extends TimerTask {
    	private Game game;
    	
    	public EndInteractionTask(Game game){
    		this.game = game;
    	}
    	
		@Override
		public void run() {
			game.StartSelectionOfHostages();	
		}
    	
    }	
}
