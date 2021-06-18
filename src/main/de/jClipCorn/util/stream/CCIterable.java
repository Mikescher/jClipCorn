package de.jClipCorn.util.stream;

public interface CCIterable<T> extends Iterable<T> {
	CCStream<T> ccstream();
}
