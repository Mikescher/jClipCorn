package de.jClipCorn.util.lambda;

@FunctionalInterface
public interface Func0to0WithException<TException extends Throwable> {
	void invoke() throws TException;
}
