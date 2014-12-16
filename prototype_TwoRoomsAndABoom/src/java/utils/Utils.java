package utils;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class Utils {
	
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

}
