package de.jClipCorn.gui.mainFrame.toolbar;

import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.UIActionTree;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.helper.KeyStrokeUtil;

import javax.swing.*;

public class AbstractClipToolbar extends JToolBar {
	private static final long serialVersionUID = -8713389540339519009L;

	protected JButton addAction(UIActionTree tree, String actionIdent) {
		final CCActionElement el = tree.find(actionIdent);
		
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
		
		tmp.addActionListener(arg0 -> el.execute(MainFrame.getInstance(), ActionSource.TOOLBAR, MainFrame.getInstance().getSelectedElement(), null));
		
		add(tmp);
		
		return tmp;
	}
}
