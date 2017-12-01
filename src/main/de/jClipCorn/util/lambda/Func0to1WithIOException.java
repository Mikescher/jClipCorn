package de.jClipCorn.util.lambda;

import java.io.IOException;

public interface Func0to1WithIOException<T> {
	T get() throws IOException;
}
