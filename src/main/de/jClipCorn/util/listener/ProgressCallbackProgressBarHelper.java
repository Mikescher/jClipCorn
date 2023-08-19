package de.jClipCorn.util.listener;

import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;

public class ProgressCallbackProgressBarHelper implements ProgressCallbackListener{
	private final JProgressBar progressBar;
	private final int resolution;

	private long value;
	private long max;

	public ProgressCallbackProgressBarHelper(JProgressBar p, int resolution) {
		this.progressBar = p;
		this.resolution = Math.max(resolution, 1) * 2;
		this.value = 0;
		SwingUtils.invokeLater(() -> progressBar.setValue(0));
		this.max = 100;
	}

	@Override
	public void step() {
		step(1);
	}

	@Override
	public void step(final long inc) {
		int pold = (int)((value*1.0*resolution) / (max));
		value += inc;
		int pnew = (int)((value*1.0*resolution) / (max));
		if (pold != pnew) SwingUtils.invokeLater(() -> progressBar.setValue(pnew));
	}

	@Override
	public void stepToMax() {
		int pold = (int)((value*1.0*resolution) / (max));
		value = max;
		int pnew = (int)((value*1.0*resolution) / (max));
		if (pold != pnew) SwingUtils.invokeLater(() -> progressBar.setValue(pnew));
	}

	@Override
	public void setMax(final long max) {
		SwingUtils.invokeAndWaitConditional(() -> progressBar.setMaximum(resolution));
		this.max = max;
	}

	@Override
	public void reset() {
		SwingUtils.invokeAndWaitConditional(() -> progressBar.setValue(0));
		this.value = 0;
	}
}
