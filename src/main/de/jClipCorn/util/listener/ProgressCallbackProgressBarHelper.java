package de.jClipCorn.util.listener;

import de.jClipCorn.features.log.CCLog;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

public class ProgressCallbackProgressBarHelper implements ProgressCallbackListener{
	private final JProgressBar progressBar;
	private final int resolution;

	private int value;
	private int max;

	public ProgressCallbackProgressBarHelper(JProgressBar p, int resolution) {
		this.progressBar = p;
		this.resolution = Math.max(resolution, 1) * 2;
		this.value = 0;
		SwingUtilities.invokeLater(() -> progressBar.setValue(0));
		this.max = 100;
	}

	@Override
	public void step() {
		int pold = (int)((value*1.0*resolution) / (max));
		value++;
		int pnew = (int)((value*1.0*resolution) / (max));
		if (pold != pnew) SwingUtilities.invokeLater(() -> progressBar.setValue(value));
	}

	@Override
	public void setMax(final int max) {
		try {
			SwingUtilities.invokeAndWait(() -> progressBar.setMaximum(max));
			this.max = max;
		} catch (InterruptedException | InvocationTargetException e) {
			CCLog.addUndefinied(Thread.currentThread(), e);
		}
	}

	@Override
	public void reset() {
		try {
			SwingUtilities.invokeAndWait(() -> progressBar.setValue(0));
			this.value = 0;
		} catch (InterruptedException | InvocationTargetException e) {
			CCLog.addUndefinied(Thread.currentThread(), e);
		}
	}
}
