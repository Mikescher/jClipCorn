package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func1to1WithGenericException<TInput1, TResult, TException extends Throwable> {
	TResult invoke(TInput1 value) throws TException;
}
