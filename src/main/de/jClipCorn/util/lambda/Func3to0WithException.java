package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func3to0WithException<TInput1, TInput2, TInput3> {
	void invoke(TInput1 value1, TInput2 value2, TInput3 value3) throws Exception;
}
