package de.jClipCorn.util.stream;

public abstract class CCSimpleStream<TType> extends CCStream<TType> {

	private boolean initialized = false;
	private TType current;
	private boolean alive = true;
	private boolean deferredDeath = false;
	
	@Override
	public boolean hasNext() {
		if (! initialized) initFirst();
		return alive;
	}

	@Override
	public TType next() {
		if (! initialized) initFirst();
		
		if (! alive) throw new CCDeadStreamError();
		
		if (deferredDeath) {
			alive = false;
			TType value = current;
			current = null;
			return value;
		}
		
		TType value = current;
		
		current = nextOrNothing(false);
		if (! alive) current = null;
		
		return value;
	}

	private void initFirst() {
		current = nextOrNothing(true);
		initialized = true;
	}
	
	protected <T> T finishStream() {
		alive = false;
		return null;
	}
	
	protected void finishStreamAfterThis() {
		deferredDeath = true;
	}
	
	public abstract TType nextOrNothing(boolean first);
}
