// Internal action code for project prototype_TwoRoomsAndABoom

package proto0.actions;

import java.text.SimpleDateFormat;
import java.util.Date;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class give_place extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        //ts.getAg().getLogger().info("executing internal action 'actions.calculate_roles'");
        /*
        if (true) { // just to show how to throw another kind of exception
            throw new JasonException("not implemented!");
        }
        */

        ListTermImpl list = new ListTermImpl();
        list.add(LiteralImpl.parseLiteral("role(blue,president)"));
        list.add(LiteralImpl.parseLiteral("role(red,bomber)"));        
        NumberTerm num_per_team = (NumberTerm)args[0];
        int num = (int) num_per_team.solve();
        
        for(int i=0; i<(num-2)/2; i++){
        	list.add(LiteralImpl.parseLiteral("role(blue,normal)"));
        	list.add(LiteralImpl.parseLiteral("role(red,normal)"));
        }
		return un.unifies(args[1], list);        
    }
}
