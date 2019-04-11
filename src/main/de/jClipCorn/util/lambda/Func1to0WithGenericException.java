package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func1to0WithGenericException<TInput1, TException extends Throwable> {
	void invoke(TInput1 value) throws TException;
}
