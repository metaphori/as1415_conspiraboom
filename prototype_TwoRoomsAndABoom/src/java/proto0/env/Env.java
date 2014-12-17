// Environment code for project prototype_TwoRoomsAndABoom
package proto0.env;
import jason.asSyntax.*;
import jason.environment.*;

import java.util.List;
import java.util.logging.*;

import proto1.game.config.Rooms;
import proto1.game.impl.Player;

public class Env extends Environment {

	/* Every type of object in the env is represented by a bit mask: an agent is 000010; an obstacle is 000100; */
	public static final int PLAYER = 16;
	public static final int LEADER = 32;
	public static final int DOOR = 64;	
	
	public static final String ROOM1 = "room1";
	public static final String ROOM2 = "room2";	
	
	/* Action literals */
	public static class Actions {
		public static final String F_go_to_room = "go_to_room";
	}
	
	public static enum GamePhase {
		Setup,
		LeaderSelection,
		Interaction,
		HostageExchange,
		End
	};
	
    private Logger logger = Logger.getLogger("proto0_TwoRoomsAndABoom."+Env.class.getName());

    protected EnvModel model;
    protected EnvView view;
    
    protected final static int SIZE = 10;
    
    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        //addPercept(Literal.parseLiteral("percept(demo)"));
        
        model = new EnvModel(SIZE);
        if(args.length>=2 && args[1].equals("gui")){
        	view = new EnvView(model);
        	model.setView(view);
        }
        
        updatePercepts();
    }

    public void updatePercepts(){

    	/*
    	// get the robot location
    	Location lRobot = model.getAgPos(0);
    	// the robot can perceive where it is
    	if (lRobot.equals(model.lFridge)) {
    		addPercept("robot", af);
    	}    	
    	*/
    }
    
    @Override
    public boolean executeAction(String agName, Structure action) {
    	boolean result = true;

    	List<Term> terms = action.getTerms();
    	String functor = action.getFunctor();

    	if(functor.equals(Actions.F_go_to_room)){
    		String agentName = terms.get(0).toString();
    		String room = terms.get(1).toString();
    		logger.info("Agent " + agentName + " wants to go to " + room);
    		result = model.moveAgent(agentName, room);
    	} else{
    		logger.info("executing: "+action+", but not implemented!");
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

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
