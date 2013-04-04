package de.jClipCorn.util;

public interface ProgressCallbackListener {
	public void step();
	public void setMax(final int max);
	public void reset();
}
