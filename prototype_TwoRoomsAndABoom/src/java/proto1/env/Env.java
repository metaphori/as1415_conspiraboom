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
import proto1.game.impl.Player;
import proto1.game.impl.Room;
import proto1.game.impl.Rooms;
import proto1.game.impl.Team;
import proto1.game.impl.Turn;
import proto1.game.interfaces.IPlayer;
import proto1.game.interfaces.IRoom;
import proto1.game.interfaces.ITurn;
import utils.Tuple;

public class Env extends Environment implements Observer {

	/* Every type of object in the env is represented by a bit mask: an agent is 000010; an obstacle is 000100; */
	public static final int PLAYER = 16;
	public static final int LEADER = 32;
	public static final int DOOR = 64;	
	
	/* Action literals */
	public static class Actions {
		//public static final String F_go_to_room = "go_to_room";
		public static final String WANNA_PLAY = "wanna_play";
		public static final String VOTE_LEADER = "vote";
		public static final String SELECT_HOSTAGE = "select_hostage";
		public static final String OK_I_AM_DONE = "ok_i_am_done";
	}
	
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
        if(args.length>=1){
        	num_players = Integer.parseInt(args[0]);
        }
        
        this.game = new Game(num_players);
        this.game.addObserver(this);
        
        model = new EnvModel(this.game, SIZE);
        if(args.length>=2 && args[1].equals("gui")){
        	view = new EnvView(model);
        	model.setView(view);
        }
        
        updatePercepts();
    }

    public void updatePercepts(){
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
    	boolean result = false;

    	List<Term> terms = action.getTerms();
    	String functor = action.getFunctor();
    	Player p = game.getPlayerFromName(agName);
    	
    	if(functor.equals(Actions.WANNA_PLAY) && game.IsAt(GamePhase.Init) ){
    		result = game.AddPlayer(agName);
    	} else if(functor.equals(Actions.VOTE_LEADER) && game.IsAt(GamePhase.LeaderSelection)){
    		String candidateLeader = ((Atom)terms.get(0)).toString();
    		result = game.Vote(agName, candidateLeader);
    	} else if(functor.equals(Actions.SELECT_HOSTAGE) && game.IsAt(GamePhase.HostageSelection)){
    		String hostage = ((Atom)terms.get(0)).toString();
    		result = game.SelectHostage(p, hostage);
    	} else if(functor.equals(Actions.OK_I_AM_DONE) && game.IsAt(GamePhase.Interaction)){
    		result = game.advanceTurnInRoom(p.getRoom());	
    	}

    	// only if action completed successfully, update agents' percepts
		if (result) {
			updatePercepts();
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
		return result;
		
    }  
    
    
    public class EndInteractionTask extends TimerTask {
    	private Game game;
    	
    	public EndInteractionTask(Game game){
    		this.game = game;
    	}
    	
		@Override
		public void run() {
			game.EndInteraction();	
		}
    	
    }
	
    
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }

	public void update(Observable obj, Object arg) {
		Game game = (Game)obj;
		Game.NotifyEvents event = (Game.NotifyEvents)arg;
		if(event instanceof Game.GAME_PHASE_COMPLETED){
			Game.GAME_PHASE_COMPLETED ev = (Game.GAME_PHASE_COMPLETED)event;
			GamePhase completed_phase = ev.phase;
			
			switch(completed_phase){
				/*******************************************************/
				/* SETUP PERFORMED: roles and rooms have been assigned */
				case Init:
					clearAllPercepts();
					logger.info("Update from game: complete phase INIT");
					game.SetupNewRound();
					break;
				case SetupRound:
					logger.info("Update from game: complete phase SETUP for round " + game.round);

					removePerceptsByUnif(Literal.parseLiteral("room(_,_)[source(_)]"));					
					
					for(Player p : game.players){
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
				/********************************************************/
				/* LEADER SELECTION PERFORMED: both rooms have a leader */
				case LeaderSelection:
					logger.info("Update from game: complete phase LEADER SELECTION");

					// communicate leader to all room's players
		    		for(Room room : Rooms.asList()){
		    			Player leader = room.getLeader();
		    			logger.info("Leader of " + room + " is " + leader);
		    			Literal literal = Literal.parseLiteral("room_leader("+leader.getName()+")");
		    			AddPerceptInRoom(room, literal);					
		    		}
		    		
		    		//game.ProceedWithInteraction();
		    		//new Timer().schedule(new EndInteractionTask(game), getInteractionTime(game.round));
		    		
		    		// Give a start to players!
		    		removePerceptsByUnif(Literal.parseLiteral("phase(_)[source(_)]"));
		    		addPercept(Literal.parseLiteral("phase(interaction)"));		 
		    		
		    		game.ProceedWithInteraction();
					break;
					/********************************************************/
					/* INTERACTION PERFORMED */					
				case Interaction:
					logger.info("Update from game: complete phase INTERACTION");
					removePerceptsByUnif(Literal.parseLiteral("phase(_)"));
		    		addPercept(Literal.parseLiteral("phase(hostages_exchange)"));   					
					break;
					/********************************************************/
					/* HOSTAGE SELECTION PERFORMED: ready to swap */					
				case HostageSelection:
					logger.info("Update from game: complete phase HOSTAGE SELECTION");
					game.SetupNewRound();		
					break;
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
    		IRoom room = turnev.room;
    		ITurn turn = turnev.turn;
    		
    		Literal removeliteral = Literal.parseLiteral("turn(_)[source(_)]");
    		RemovePerceptInRoom(room, removeliteral);
    		
    		Literal literal = Literal.parseLiteral("turn("+game.currentTurnInRoom(room).getName()+")");
		    AddPerceptInRoom(room, literal);	
		}
	} // end update() method
	
	public void AddPerceptInRoom(IRoom room, Literal literal){
		for(Player p : game.getPlayersInRoom(room)){
			addPercept(p.getName(), literal);
		}		
	}
	
	public void RemovePerceptInRoom(IRoom room, Literal literal){
		for(Player p : game.getPlayersInRoom(room)){
			removePerceptsByUnif(p.getName(), Literal.parseLiteral("turn(_)[source(_)]"));
		}
	}
	
	public int getInteractionTime(int round){
		return 1000;
	}
}
