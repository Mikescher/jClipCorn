package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func1to1<TInput1, TResult> {
	TResult invoke(TInput1 value);
}
