package de.jClipCorn.util.lambda;

import java.io.IOException;

@FunctionalInterface
public interface Func0to0WithIOException {
	void invoke() throws IOException;
}
