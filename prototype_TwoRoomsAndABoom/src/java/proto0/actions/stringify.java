// Internal action code for project prototype_TwoRoomsAndABoom

package proto0.actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class stringify extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	Term t = args[0];
    	if(t instanceof ListTerm){
	        ListTerm lt = (ListTerm)args[0];
	        String str = "";
	        for(Term st : lt){
	        	str += st.toString();
	        }
	        return un.unifies(args[1], new StringTermImpl(str));
    	} else{
    		return un.unifies(args[1], new StringTermImpl(t.toString()));
    	}
    }
}
