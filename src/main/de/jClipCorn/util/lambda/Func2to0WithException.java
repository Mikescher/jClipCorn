package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func2to0WithException<TInput1, TInput2> {
	void invoke(TInput1 value1, TInput2 value2) throws Exception;
}
