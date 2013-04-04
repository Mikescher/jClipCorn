package de.jClipCorn.util;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressCallbackHelper implements ProgressCallbackListener{
	private final JProgressBar progressBar;
	
	public ProgressCallbackHelper(JProgressBar p) {
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
