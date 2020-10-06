package de.jClipCorn.util.listener;

public interface ProgressCallbackListener {
	void step();
	void step(final int inc);
	void stepToMax();
	void setMax(final int max);
	void reset();

}
