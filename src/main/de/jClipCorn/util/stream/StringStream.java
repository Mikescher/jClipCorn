package de.jClipCorn.util.stream;

public class StringStream extends CCStream<Character> {

	private final String str;
	private int pos = 0;
	
	public StringStream(String _str) {
		super();
		str = _str;
	}
	
	@Override
	public boolean hasNext() {
		return pos < str.length();
	}

	@Override
	public Character next() {
		return str.charAt(pos++);
	}

	@Override
	protected CCStream<Character> cloneFresh() {
		return new StringStream(str);
	}

}
