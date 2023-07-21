package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func0to1WithException<TResult> {
	TResult invoke() throws Exception;
}
