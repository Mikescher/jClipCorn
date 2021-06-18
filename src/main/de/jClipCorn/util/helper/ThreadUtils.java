package de.jClipCorn.util.helper;

import javax.swing.*;

public class ThreadUtils {
	public static void delay(int ms, Runnable r) {
		new Thread(() -> { ThreadUtils.safeSleep(ms); SwingUtils.invokeLater(r); }).start();
	}

	public static void setProgressbarAndWait(JProgressBar pbar, int val, int min, int max) {
		SwingUtils.invokeAndWaitSafe(() -> { pbar.setMinimum(min); pbar.setMaximum(max); pbar.setValue(val); });
	}
	
	public static void setProgressbarAndWait(JProgressBar pbar, int val) {
		SwingUtils.invokeAndWaitSafe(() -> { pbar.setValue(val); });
	}

	@SuppressWarnings("StatementWithEmptyBody")
	public static void safeSleep(int millis) {
		long s = System.currentTimeMillis();
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			while (System.currentTimeMillis() - s < millis) { /* */ }
		}
	}
}
