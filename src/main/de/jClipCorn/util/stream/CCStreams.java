package de.jClipCorn.util.stream;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public final class CCStreams {
	private CCStreams() {
		throw new InstantiationError();
	}

	@SuppressWarnings("rawtypes")
	public static CCStream<Object> iterateList(List ls) {
		return new ArrayStream<>(ls.toArray());
	}

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
		return new CounterStream(start, end - start); // start included | end excluded
	}

	public static <K, V> CCStream<Map.Entry<K, V>> iterate(Map<K, V> map) {
		return new IterableStream<>(map.entrySet());
	}
}
