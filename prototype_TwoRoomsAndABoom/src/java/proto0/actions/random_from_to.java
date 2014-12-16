// Internal action code for project prototype_TwoRoomsAndABoom

package proto0.actions;

import java.util.Random;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class random_from_to extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {     
        NumberTerm lbt = (NumberTerm)args[0];
        NumberTerm ubt = (NumberTerm)args[1];
        int lb = (int) lbt.solve();
        int ub = (int) ubt.solve();
        
        Random randgen = new Random();
        int num = lb;
        num += randgen.nextInt(ub-lb);
        NumberTerm res = new NumberTermImpl(num);
		return un.unifies(args[2], res); 
    }
}
