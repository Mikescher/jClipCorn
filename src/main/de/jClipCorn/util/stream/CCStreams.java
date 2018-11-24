package de.jClipCorn.util.stream;

import java.util.Collections;
import java.util.Enumeration;

public final class CCStreams {
	private CCStreams() { throw new InstantiationError(); }

	public static <T> CCStream<T> iterate(Iterable<T> ls) {
		return new IterableStream<>(ls);
	}

	public static <T> CCStream<T> iterate(T[] ls) {
		return new ArrayStream<>(ls);
	}

	public static CCStream<Byte> iterate(byte[] ls) {
		return new ByteArrayStream(ls);
	}

	public static <T> CCStream<T> single(T ls) {
		return new SingleStream<>(ls);
	}

	public static <T> CCStream<T> empty() {
		return new EmptyStream<>();
	}

	public static <T> CCStream<T> iterate(Enumeration<T> ls) {
		return iterate(Collections.list(ls));
	}

	public static CCStream<Character> iterate(String str) {
		return new StringStream(str);
	}

	public static CCStream<Integer> countRange(int start, int end) {
		return new CounterStream(start, end-start); // start included | end excluded
	}
}
