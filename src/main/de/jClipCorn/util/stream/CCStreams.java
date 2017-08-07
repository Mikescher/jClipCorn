package de.jClipCorn.util.stream;

public final class CCStreams {
	private CCStreams() { throw new InstantiationError(); }

	public static <T> CCStream<T> iterate(Iterable<T> ls) {
		return new IterableStream<>(ls);
	}

	public static <T> CCStream<T> iterate(T[] ls) {
		return new ArrayStream<>(ls);
	}

	public static <T> CCStream<T> single(T ls) {
		return new SingleStream<>(ls);
	}

	public static <T> CCStream<T> empty() {
		return new EmptyStream<>();
	}
}
