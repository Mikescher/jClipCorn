package de.jClipCorn.util.helper;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ThreadUtils {
	public static boolean invokeAndWaitSafe(Runnable r) {
		try {
			SwingUtilities.invokeAndWait(r);
			return true;
		} catch (InvocationTargetException | InterruptedException e) {
			return false;
		}
	}
	
	public static void setProgressbarAndWait(JProgressBar pbar, int val, int min, int max) {
		invokeAndWaitSafe(() -> { pbar.setMinimum(min); pbar.setMaximum(max); pbar.setValue(val); });
	}
	
	public static void setProgressbarAndWait(JProgressBar pbar, int val) {
		invokeAndWaitSafe(() -> { pbar.setValue(val); });
	}
}
