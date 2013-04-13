package de.jClipCorn.gui.frames.mainFrame.clipToolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import de.jClipCorn.gui.actionTree.CCActionElement;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.KeyStrokeUtil;

public class AbstractClipToolbar extends JToolBar {
	private static final long serialVersionUID = -8713389540339519009L;

	protected JButton addAction(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return null;
		}
		
		JButton tmp = new JButton(el.getIcon());

		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			tmp.setToolTipText("<html>" + el.getCaption() + "&nbsp;&nbsp;&nbsp;&nbsp;<small>" + KeyStrokeUtil.keyStrokeToString(el.getKeyStroke()) + "</small></html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			tmp.setToolTipText(el.getCaption());
		}
		
		tmp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				el.execute();
			}
		});
		
		add(tmp);
		
		return tmp;
	}
}
