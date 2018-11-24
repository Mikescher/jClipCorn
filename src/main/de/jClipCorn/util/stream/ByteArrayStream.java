package de.jClipCorn.util.stream;

public class ByteArrayStream extends CCStream<Byte> {

	private final byte[] arr;
	private int pos = 0;
	
	public ByteArrayStream(byte[] _arr) {
		super();
		arr = _arr;
	}
	
	@Override
	public boolean hasNext() {
		return pos < arr.length;
	}

	@Override
	public Byte next() {
		return arr[pos++];
	}

	@Override
	protected CCStream<Byte> cloneFresh() {
		return new ByteArrayStream(arr);
	}

}
