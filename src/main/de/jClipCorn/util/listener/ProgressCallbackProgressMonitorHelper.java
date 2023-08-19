package de.jClipCorn.util.listener;

import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;

public class ProgressCallbackProgressMonitorHelper implements ProgressCallbackListener
{
	private final static int REAL_MAX = 65536;

	private final ProgressMonitor monitor;
	private long progress;
	private long max;
	
	public ProgressCallbackProgressMonitorHelper(ProgressMonitor p) {
		this.monitor = p;
		progress = 0;
		max = 1;
	}

	@Override
	public void stepToMax() {
		progress = max;

		update();}

	@Override
	public void step() {
		step(1);
	}

	@Override
	public void step(final long inc) {
		progress += inc;

		update();
	}

	@Override
	public void setMax(final long pmax) {
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

		SwingUtils.invokeLater(() ->
		{
			// ProgressMonitor only supports int - shrink down to 16bit
			var monitorVal = (max==0) ? 0 : (int)Math.round((progress*REAL_MAX)/(1.0*max));

			monitor.setMaximum(REAL_MAX);
			monitor.setNote(message);
			monitor.setProgress(monitorVal);
		});
	}
}
