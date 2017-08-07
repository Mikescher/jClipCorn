package de.jClipCorn.util.stream;

public class ArrayStream<TType> extends CCStream<TType> {

	private final TType[] arr;
	private int pos = 0;
	
	public ArrayStream(TType[] _arr) {
		super();
		arr = _arr;
	}
	
	@Override
	public boolean hasNext() {
		return pos < arr.length;
	}

	@Override
	public TType next() {
		return arr[pos++];
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new ArrayStream<>(arr);
	}

}
