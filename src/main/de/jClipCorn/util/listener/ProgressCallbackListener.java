package de.jClipCorn.util.listener;

public interface ProgressCallbackListener {
	void step();
	void setMax(final int max);
	void reset();
}
