package de.jClipCorn.util.datatypes;

public interface IOpt<TData> {
	boolean isPresent();
	boolean isEmpty();
	TData get();
}
