// Internal action code for project prototype_TwoRoomsAndABoom

package iactions;

import java.util.Random;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class intersect extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        ListTerm lst1 = (ListTerm)args[0];
        ListTerm lst2 = (ListTerm)args[1];
        
        ListTerm res = new ListTermImpl();
        
        for(int i=0; i < lst1.size(); i++){
        	String item = lst1.get(i).toString();
        	boolean contained = false;
        	for(int k=0; k<lst2.size() && !contained; k++){
        		if(lst2.get(k).equals(item))
        			contained = true;
        	}
        	if(contained) res.add(lst1.get(i));
        }
        
		return un.unifies(args[2], res);         
    }
}
