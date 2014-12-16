// Internal action code for project prototype_TwoRoomsAndABoom

package proto0.actions;

import java.util.Hashtable;
import java.util.Random;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class scrutinize extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	// Vote structure ==> vote(in(Room), from(Player), to(Candidate))
        ListTerm votes_term = (ListTerm)args[0];
        Atom strategy_atom = (Atom)args[1];
        
        // 1) Count votes
        Hashtable<String, Integer> votes = new Hashtable<String, Integer>();
        String winner = null;
        int maxvotes = -1;
        boolean parity = true;
        for(Term t : votes_term.getAsList()){
        	Structure voted = (Structure) ((Structure)t).getTerm(2);
        	String votedplayer = ((Atom)voted.getTerm(0)).toString();
        	Integer previous = votes.get(votedplayer);
        	votes.put(votedplayer, previous==null ? 1 : 1+previous);
        	if(previous==null && winner==null){
        		winner = votedplayer;
        		maxvotes = 1;
        		parity = false;
        	} else if(previous!=null && maxvotes < (previous+1)){
        		winner = votedplayer;
        		maxvotes = previous+1;
        		parity = false;
        	} else if(previous!=null && maxvotes == (previous+1)){
        		parity = true;
        	}
        }
        
        if(strategy_atom.toString().equals("random_if_parity")){
        	// Ok, keep winner
        }
        
        Atom winnerAtom = new Atom(winner);
        NumberTerm withNumVotes = new NumberTermImpl(maxvotes);
        un.unifies(args[2], winnerAtom);
		return un.unifies(args[3], withNumVotes); 
    }
}
