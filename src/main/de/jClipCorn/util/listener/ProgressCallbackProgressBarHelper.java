package de.jClipCorn.util.listener;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressCallbackProgressBarHelper implements ProgressCallbackListener{
	private final JProgressBar progressBar;
	
	public ProgressCallbackProgressBarHelper(JProgressBar p) {
		this.progressBar = p;
	}

	@Override
	public void step() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setValue(progressBar.getValue() + 1);
			}
		});
	}

	@Override
	public void setMax(final int max) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setMaximum(max);
			}
		});
	}

	@Override
	public void reset() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setValue(0);
			}
		});
	}
}
