package de.jClipCorn.util.listener;

public class ProgressCallbackSink implements ProgressCallbackListener{

	public ProgressCallbackSink() { }

	@Override
	public void step() { /* NOP */ }

	@Override
	public void step(final long inc) { /* NOP */ }

	@Override
	public void stepToMax() { /* NOP */ }

	@Override
	public void setMax(final long max) { /* NOP */ }

	@Override
	public void reset() { /* NOP */ }
}
