package de.jClipCorn.util.listener;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;

public class ProgressCallbackProgressMonitorHelper implements ProgressCallbackListener
{
	private final static int REAL_MAX = 1000;

	private final ProgressMonitor monitor;
	private final boolean skipNoop;

	private String _currCache = Str.Empty;

	private long progress;
	private long max;
	
	public ProgressCallbackProgressMonitorHelper(ProgressMonitor p, boolean skipNoop) {
		this.monitor = p;
		this.skipNoop = skipNoop;

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
	
	private void update()
	{
		// ProgressMonitor only supports int - shrink down to 16bit
		var monitorVal = (max==0) ? 0 : (int)Math.round((progress*REAL_MAX)/(1.0*max));

		if (skipNoop)
		{
			String newCache = String.format("%d | %d | %.1f", monitorVal, max, getPercentage()); //$NON-NLS-1$
			if (Str.equals(newCache, _currCache)) return; // nothing changed, update is unneccessary
			_currCache = newCache;
		}

		final String message = String.format("Completed %.1f%%.\n", getPercentage()); //$NON-NLS-1$

		SwingUtils.invokeLater(() ->
		{


			monitor.setMaximum(REAL_MAX);
			monitor.setNote(message);
			monitor.setProgress(monitorVal);
		});
	}
}
