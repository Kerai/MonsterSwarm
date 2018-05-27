package eu.keray.swarm;

import java.util.*;
import java.util.Map.Entry;

public class ValueMap<K>{

	private final HashMap<K, Value> values;
	
	
	public ValueMap(int capacity) {
		values = new HashMap<K, Value>(capacity, 0.9f);
	}
	
	static class Value {
		int val;
	}
	
	Iterator<Entry<K, Value>> iter;
	/** start iterating */ // lol so 90's
	public void iter() {
		iter = values.entrySet().iterator();
	}
	
	public K key;
	public Value val;
	
	public boolean next() {
		if(!iter.hasNext())
			return false;
		Entry<K, Value> next = iter.next();
		key = next.getKey();
		val = next.getValue();
		return true;
	}
	public void remove() {
		iter.remove();
	}
	
	public interface ReduceObserver<K> {
		public void removed(K key);
	}
	
	public void reduce(ReduceObserver<K> obs) {
		Iterator<Entry<K, Value>> iter = values.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<K, Value> next = iter.next();
			Value v = next.getValue();
			if(--v.val < 1) {
				if(obs!=null)
					obs.removed(next.getKey());
				iter.remove();
			}
		}
	}
	
	public int remove(K key, int def) {
		Value v = values.remove(key);
		if(v == null) {
			return def;
		}
		return v.val;
	}
	
	public void put(K key, int value) {
		Value v = values.get(key);
		if(v == null) {
			v = new Value();
			values.put(key, v);
		}
		v.val = value;
	}
	
	public int increment(K key, int increment) {
		Value v = values.get(key);
		if(v == null) {
			v = new Value();
			values.put(key, v);
		}
		int old = v.val;
		v.val+= increment;
		return old;
	}
	
	public int incrementExisting(K key, int increment) {
		Value v = values.get(key);
		if(v == null) {
			return -1;
		}
		return v.val++;
	}

	public int size() {
	    return values.size();
    }
	
	public void clear() {
		values.clear();
	}
	
	public Set<K> keys() {
		return values.keySet();
	}
}
