package utils;

import jason.asSemantics.Option;
import jason.util.Pair;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

//import org.apache.commons.math3.random.RandomDataGenerator;

public class Utils {
	
	//public static final RandomDataGenerator random = new RandomDataGenerator();

	public static <K,V> Hashtable<K, V> RandomMatch(List<K> lst1, List<V> lst2){
		Hashtable<K, V> hash = new Hashtable<K, V>();
		Random random = new Random();
		int N = lst1.size();
		for(int i=0; i<N; i++){
			int next = random.nextInt(N-i);
			V obj = lst2.remove(next);
			hash.put(lst1.get(i), obj);
		}
		return hash;
	}

	public static Option RandomSample(List<Pair<Option, Double>> lst) {
		Random rand = new Random();
		List<Option> corpus = BuildCorpus(lst,10);
		return corpus.get(rand.nextInt(corpus.size()));
	}
	
	public static <T> List<T> BuildCorpus(List<Pair<T,Double>> lst, int sz){
		List<T> corpus = new ArrayList<T>();
		double min = 1;
		for(Pair<T,Double> pair : lst){
			double d = pair.getSecond();
			if(d<min) { min = d; }
		}
		double multiplier = 0.100001 / min;
		int ntimes = sz;
		for(int k=0; k<ntimes; k++){
			for(Pair<T,Double> pair : lst){
				double d = pair.getSecond();
				T opt = pair.getFirst();
				if(d>1){ d /= d+1; } // normalize
				d = d * multiplier * 10;
				long num_of_occurr = Math.round(d);
				for(int z=0;z<num_of_occurr;z++){
					corpus.add(opt);
				}
			}
		}
		return corpus;
	}
	
	public static void main(String[] args){
		List<Pair<String,Double>> lst = new ArrayList<Pair<String,Double>>();
		lst.add(new Pair("aaa",0.1));
		lst.add(new Pair("fff",0.3));
		lst.add(new Pair("zzz", 0.6));
		for(int i=1; i<100; i*=5){
			List<String> corpus = BuildCorpus(lst,i);
			System.out.println(corpus);
		}
		
		Random rand = new Random();
		List<String> corpus = BuildCorpus(lst,50);
		int sz = corpus.size();
		int aocc = 0;
		int focc = 0;
		int zocc = 0;
		for(int i=0; i<100; i++){
			String s = corpus.get(rand.nextInt(sz));
			System.out.println(s);
			if(s.equals("aaa")) aocc++;
			if(s.equals("fff")) focc++;
			if(s.equals("zzz")) zocc++;
		}
		System.out.println("aocc="+aocc+ "\tfocc="+focc+"\tzocc="+zocc);
	}
	
}
