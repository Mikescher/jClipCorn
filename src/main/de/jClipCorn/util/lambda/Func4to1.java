package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func4to1<TInput1, TInput2, TInput3, TInput4, TResult> {
	TResult invoke(TInput1 value1, TInput2 value2, TInput3 value3, TInput4 value4);
}
