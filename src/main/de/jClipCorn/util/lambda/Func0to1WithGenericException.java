package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func0to1WithGenericException<TResult, TException extends Throwable> {
	TResult invoke() throws TException;
}
