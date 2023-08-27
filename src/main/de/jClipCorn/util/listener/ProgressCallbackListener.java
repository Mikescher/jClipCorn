package de.jClipCorn.util.listener;

public interface ProgressCallbackListener extends ProgressCallbackStepListener {
	void step();
	void step(final long inc);
	void stepToMax();
	void setMax(final long max);
	void reset();
}
