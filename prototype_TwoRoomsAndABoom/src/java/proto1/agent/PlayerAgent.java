package proto1.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/*
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
*/






import utils.Utils;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.Message;
import jason.asSemantics.Option;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;
import jason.runtime.Settings;
import jason.util.Pair;

public class PlayerAgent extends Agent {

	protected Logger logger = Logger.getLogger("[AgentCustomized]");
	
	public PlayerAgent(){ super(); }
	
	/**
	 * Selects socially acceptable messages.
	 * @param msg
	 * @return
	 */
	@Override
	public boolean socAcc(Message msg){
		/* An reduction to autonomy... genetically, players cannot cheat! */
		/* That is, they can communicate only if they are in the same room */
		
		String sender = msg.getSender();
		String me = msg.getReceiver();
		String sender_room = null;
		String my_room = null;
		
		if(this.bb.getPercepts()==null) return true;
		
		Iterator<Literal> it = this.bb.getPercepts();
		while(it.hasNext()){
			Literal lit = it.next();
			if(lit.getFunctor().equals("my_room") && lit.getArity()==1){
				my_room = lit.getTerm(0).toString();
			}
			if(lit.getFunctor().equals("room") && lit.getArity()==2){
				String name = lit.getTerm(0).toString();
				if(name.equals(sender)){
					sender_room = lit.getTerm(1).toString();
				}
			}
			if(my_room!=null && sender_room!=null) break;
		}
		
		if(!my_room.equals(sender_room)){
			logger.info("EHY, " + sender + " HAS TRIED TO COMMUNICATE WITH THE OTHER ROOM ("+me+")");
		}
		
		return my_room.equals(sender_room);
	}
	
	@Override
	public Option selectOption(List<Option> options){
		if(options.size()>1){
			List<Pair<Option,Double>> lst = new ArrayList<Pair<Option,Double>>();
			for(Option opt : options){
				Literal prob = opt.getPlan().getLabel().getAnnot("prob");
				if(prob!=null){
					NumberTerm probVal = (NumberTerm) prob.getTerm(0);
					double probability = Double.parseDouble(probVal.toString());
					//logger.info("Option plan " + opt.getPlan().getLabel() + " with prob " + probability /*+ 
					//		" and tag " + tag.getFunctor()*/);
					lst.add(new Pair<Option,Double>(opt, probability));
				}
			}
			
			if(lst.size()>0){
				//EnumeratedDistribution ed = new EnumeratedDistribution<Option>(lst);
				//Option result = (Option) ed.sample();
				
				Option result = Utils.RandomSample(lst);
				
				//logger.info("Sampled option ===> " + result.getPlan().getLabel());
				return result;
			} 
		}
		return options.get(0);
	}
	
}
