package de.jClipCorn.util;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;

public class DialogHelper {
	public static boolean showYesNoDlg(Component frame, String caption, String txt) {
		return JOptionPane.showConfirmDialog(frame, txt, caption, JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}
	
	public static boolean showLocaleYesNo(Component frame, String id) {
		return showYesNoDlg(frame, LocaleBundle.getString(id+"_caption"), LocaleBundle.getString(id)); //$NON-NLS-1$
	}
	
	public static void showError(Component frame, String caption, String text) {
		JOptionPane.showMessageDialog(frame, text, caption, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showLocalInformation(Component frame, String id) {
		JOptionPane.showMessageDialog(frame, LocaleBundle.getString(id), LocaleBundle.getString(id+"_caption"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
	}
	
	public static void showInformation(Component frame, String caption, String text) {
		JOptionPane.showMessageDialog(frame, text, caption, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showDispatchError(final String caption, final String text) {
		if (! SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						showError(new JFrame(), caption, text);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
			}
		} else {
			showError(new JFrame(), caption, text);
		}
	}
}
