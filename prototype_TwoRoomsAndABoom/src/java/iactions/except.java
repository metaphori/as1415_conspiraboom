// Internal action code for project prototype_TwoRoomsAndABoom

package iactions;

import java.util.Random;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class except extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        ListTerm lst = (ListTerm)args[0];
        Term diff = (Term)args[1];
        
        ListTerm res = new ListTermImpl();
        String excluded = diff.toString();
        for(int i=0; i < lst.size(); i++){
        	String item = lst.get(i).toString();
        	if(!item.equals(excluded))
        		res.add(lst.get(i));
        }
        
		return un.unifies(args[2], res);         
    }
}
