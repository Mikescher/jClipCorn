package de.jClipCorn.database.util.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReverseIterator<T> extends CCIterator<T> {
    private final ListIterator<T> it;

    public ReverseIterator(List<T> original) {
        it = original.listIterator(original.size());
    }

    @Override
	public Iterator<T> iterator() {
        return this;
    }

    @Override
	public boolean hasNext() { 
    	return it.hasPrevious(); 
    }
    
    @Override
	public T next() { 
    	return it.previous(); 
    }
    
    @Override
	public void remove() { 
    	it.remove(); 
    }
}

