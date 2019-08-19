package de.jClipCorn.util.datatypes;

public class RefParam<T> {
	public T Value;

	public RefParam(T value) {
		Value = value;
	}

	public RefParam() {
		Value = null;
	}
}
