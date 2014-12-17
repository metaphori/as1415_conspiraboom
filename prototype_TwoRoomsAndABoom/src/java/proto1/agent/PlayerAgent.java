package proto1.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
*/



import proto1.env.Env;
import utils.Utils;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.Message;
import jason.asSemantics.Option;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import jason.util.Pair;

public class PlayerAgent extends Agent {

	protected Logger logger = Logger.getLogger("[AgentCustomized]");
	
	public PlayerAgent(){
		super();
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
					lst.add(new Pair(opt, probability));
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
