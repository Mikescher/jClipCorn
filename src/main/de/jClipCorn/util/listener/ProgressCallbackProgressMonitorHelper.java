package de.jClipCorn.util.listener;

import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;

public class ProgressCallbackProgressMonitorHelper implements ProgressCallbackListener {
	private final ProgressMonitor monitor;
	private int progress;
	private int max;
	
	public ProgressCallbackProgressMonitorHelper(ProgressMonitor p) {
		this.monitor = p;
		progress = 0;
		max = 1;
	}

	@Override
	public void step() {
		progress++; 
		
		update();
	}

	@Override
	public void setMax(final int pmax) {
		max = pmax;
		
		update();
	}

	@Override
	public void reset() {
		progress = 0;
		
		update();
	}
	
	private double getPercentage() {
		if (max != 0) {
			return (progress / (max * 1.0)) * 100;
		} else {
			return 0;
		}
	}
	
	private void update() {
		final String message = String.format("Completed %.1f%%.\n", getPercentage()); //$NON-NLS-1$

		SwingUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				monitor.setMaximum(max);
				monitor.setNote(message);
				monitor.setProgress(progress);
			}
		});
	}
}
