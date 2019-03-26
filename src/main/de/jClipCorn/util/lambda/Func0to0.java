package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func0to0 {
	Func0to0 NOOP = () -> { /* */ };

	void invoke();
}
