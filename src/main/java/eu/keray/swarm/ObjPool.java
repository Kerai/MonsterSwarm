package eu.keray.swarm;

import java.lang.reflect.Array;

@SuppressWarnings("unchecked")
public class ObjPool<T> {
	
	Class<T> clazz;
	T[] stack;
	private int size;
	
    public ObjPool(Class<T> clazz) {
		this.clazz = clazz;
		stack = (T[]) Array.newInstance(clazz, 128);
	}
	
	public T take() {
        try {
    		if(size<=0)
    			return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
		
        return stack[--size];
	}
	
	private void ensureSize(int size) {
		if(stack.length < size) {
			int newsize = Math.max((int)(stack.length * 1.4f), stack.length + 10);
			T[] newstack = (T[]) Array.newInstance(clazz, newsize);
			System.arraycopy(clazz, 0, newstack, 0, this.size);
			stack = newstack;
		}
	}
	
	public void give(T obj) {
		ensureSize(size+1);
		stack[size++] = obj;
	}
}
