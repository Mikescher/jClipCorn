package de.jClipCorn.util.lambda;

import java.io.IOException;

@FunctionalInterface
public interface Func0to1WithIOException<TResult> {
	TResult invoke() throws IOException;
}
