package de.jClipCorn.util.helper;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;

public class DialogHelper {
	public static boolean showYesNoDlg(Component frame, String caption, String txt) {
		return JOptionPane.showConfirmDialog(frame, txt, caption, JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}

	public static boolean showYesNoDlgDefaultNo(Component frame, String caption, String txt) {
		return JOptionPane.showOptionDialog(
				frame,
				txt,
				caption,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				new Object[]{UIManager.getString("OptionPane.yesButtonText"), UIManager.getString("OptionPane.noButtonText")}, //$NON-NLS-1$ //$NON-NLS-2$
				UIManager.getString("OptionPane.noButtonText")) == JOptionPane.OK_OPTION; //$NON-NLS-1$
	}

	public static boolean showLocaleYesNo(Component frame, String id) {
		return showYesNoDlg(frame, LocaleBundle.getString(id + "_caption"), LocaleBundle.getString(id)); //$NON-NLS-1$
	}

	public static boolean showLocaleFormattedYesNo(Component frame, String id, Object... args) {
		return showYesNoDlg(frame, LocaleBundle.getString(id + "_caption"), LocaleBundle.getFormattedString(id, args)); //$NON-NLS-1$
	}

	public static boolean showLocaleYesNoDefaultNo(Component frame, String id) {
		return showYesNoDlgDefaultNo(frame, LocaleBundle.getString(id + "_caption"), LocaleBundle.getString(id)); //$NON-NLS-1$
	}
	
	public static void showLocalError(Component frame, String id) {
		String text = LocaleBundle.getString(id);
		String caption = LocaleBundle.getString(id + "_caption"); //$NON-NLS-1$
		SwingUtils.invokeAndWaitConditional(() -> JOptionPane.showMessageDialog(frame==null?new JFrame():frame, text, caption, JOptionPane.ERROR_MESSAGE));
	}

	public static void showDispatchLocalInformation(Component frame, String id) {
		String text = LocaleBundle.getString(id);
		String caption = LocaleBundle.getString(id + "_caption"); //$NON-NLS-1$
		SwingUtils.invokeAndWaitConditional(() -> JOptionPane.showMessageDialog(frame==null?new JFrame():frame, text, caption, JOptionPane.INFORMATION_MESSAGE));
	}
	
	public static void showDispatchInformation(Component frame, String caption, String text) {
		SwingUtils.invokeAndWaitConditional(() -> JOptionPane.showMessageDialog(frame==null?new JFrame():frame, text, caption, JOptionPane.INFORMATION_MESSAGE));
	}

	public static void showDispatchError(Component frame, final String caption, final String text) {
		SwingUtils.invokeAndWaitConditional(() -> JOptionPane.showMessageDialog(frame==null?new JFrame():frame, text, caption, JOptionPane.ERROR_MESSAGE));
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

	public static ProgressMonitor getLocalPersistentIndeterminateProgressMonitor(Component frame, String messageIdent) {
		return getPersistentProgressMonitor(frame, LocaleBundle.getString(messageIdent), true);
	}
	public static ProgressMonitor getLocalPersistentProgressMonitor(Component frame, String messageIdent) {
		return getPersistentProgressMonitor(frame, LocaleBundle.getString(messageIdent), false);
	}
	
	private static ProgressMonitor getPersistentProgressMonitor(Component frame, String message, final boolean indeterminate) {
		final ProgressMonitor monitor = new ProgressMonitor(frame, message, "", 0, indeterminate ? 100 : 1); //$NON-NLS-1$
		monitor.setMillisToDecideToPopup(0);
		monitor.setMillisToPopup(0);
		
		SwingUtils.invokeLater(() ->
		{
			try {
				monitor.setProgress(indeterminate ? 50 : 0);

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

				if (indeterminate)
				{
					int acc = ac.getAccessibleChildrenCount();
					for (int i = 0; i < acc; i++) {
						Accessible c = ac.getAccessibleChild(i);
						if (c instanceof JProgressBar) {
							((JProgressBar)c).setIndeterminate(true);
							break;
						}
					}
				}

			} catch (Exception e) {
				CCLog.addError(LocaleBundle.getString("LogMessage.ErrorOmitingProgressMonitor"), e); //$NON-NLS-1$
			}
		});
		
		return monitor;
	}
}
