package de.jClipCorn.util.listener;

public interface ProgressCallbackListener {
	public void step();
	public void setMax(final int max);
	public void reset();
}
