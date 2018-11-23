package de.jClipCorn.util.stream;

public class CounterStream extends CCStream<Integer> {

	private final int start;
	private final int count;
	private int pos = 0;
	
	public CounterStream(int _start, int _count) {
		super();
		start = _start;
		count = _count;
	}
	
	@Override
	public boolean hasNext() {
		return pos < count;
	}

	@Override
	public Integer next() {
		return start + (pos++);
	}

	@Override
	protected CCStream<Integer> cloneFresh() {
		return new CounterStream(start, count);
	}

}
