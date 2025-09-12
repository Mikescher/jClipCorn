package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func5to1<TInput1, TInput2, TInput3, TInput4, TInput5, TResult> {
	TResult invoke(TInput1 value1, TInput2 value2, TInput3 value3, TInput4 value4, TInput5 value5);
}
