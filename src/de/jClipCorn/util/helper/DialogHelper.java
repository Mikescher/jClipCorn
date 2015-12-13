package de.jClipCorn.util.helper;

import java.awt.Component;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;

import javax.accessibility.AccessibleContext;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;

public class DialogHelper {
	public static boolean showYesNoDlg(Component frame, String caption, String txt) {
		return JOptionPane.showConfirmDialog(frame, txt, caption, JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}
	
	public static boolean showLocaleYesNo(Component frame, String id) {
		return showYesNoDlg(frame, LocaleBundle.getString(id + "_caption"), LocaleBundle.getString(id)); //$NON-NLS-1$
	}
	
	public static void showLocalError(Component frame, String id) {
		JOptionPane.showMessageDialog(frame, LocaleBundle.getString(id), LocaleBundle.getString(id + "_caption"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
	}
	
	public static void showError(Component frame, String caption, String text) {
		JOptionPane.showMessageDialog(frame, text, caption, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showLocalInformation(Component frame, String id) {
		JOptionPane.showMessageDialog(frame, LocaleBundle.getString(id), LocaleBundle.getString(id + "_caption"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
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
	
	public static int showOptions(Component frame, String caption, String text, String option1, String option2, int standard) {
		String[] oplist = {option1, option2};
		return JOptionPane.showOptionDialog(frame, text, caption, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, oplist, oplist[standard]);
	}
	
	public static int showLocaleOptions(Component frame, String ident, int standard) {
		return showOptions(frame, LocaleBundle.getString(ident + "_caption"), LocaleBundle.getString(ident), LocaleBundle.getString(ident + "_option1"), LocaleBundle.getString(ident + "_option2"), standard); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public static int showLocaleOptions(Component frame, String ident) {
		return showLocaleOptions(frame, ident, 0);
	}
	
	public static String showLocalInputDialog(Component frame, String ident, String value) {
		return JOptionPane.showInputDialog(frame, LocaleBundle.getString(ident), value);
	}
	
	public static String showPlainInputDialog(Component frame, String value) {
		return JOptionPane.showInputDialog(frame, "", value); //$NON-NLS-1$
	}
	
	public static String showPlainInputDialog(Component frame) {
		return showPlainInputDialog(frame, ""); //$NON-NLS-1$
	}
	
	public static ProgressMonitor getLocalPersistentProgressMonitor(Component frame, String messageIdent) {
		return getPersistentProgressMonitor(frame, LocaleBundle.getString(messageIdent));
	}
	
	public static ProgressMonitor getPersistentProgressMonitor(Component frame, String message) {
		final ProgressMonitor monitor = new ProgressMonitor(frame, message, "", 0, 1); //$NON-NLS-1$
		monitor.setMillisToDecideToPopup(0);
		monitor.setMillisToPopup(0);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					monitor.setProgress(0);

					AccessibleContext ac = monitor.getAccessibleContext();
					if (ac == null) throw new Exception();
					
					JDialog dialog = (JDialog) ac.getAccessibleParent();
					if (dialog == null) throw new Exception();
					
					dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					for (WindowListener listener : dialog.getWindowListeners()) {
						dialog.removeWindowListener(listener);
					}
					
					java.util.List<JButton> components = SwingUtils.getDescendantsOfType(JButton.class, dialog, true);
					if (components.isEmpty()) throw new Exception();
					
					JButton button = components.get(0);
					if (button == null) throw new Exception();
					
					button.setVisible(false);
				} catch (Exception e) {
					CCLog.addError(LocaleBundle.getString("LogMessage.ErrorOmitingProgressMonitor"), e); //$NON-NLS-1$
				}
			}
		});
		
		return monitor;
	}
}
