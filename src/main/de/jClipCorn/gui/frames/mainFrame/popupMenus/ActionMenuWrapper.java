package de.jClipCorn.gui.frames.mainFrame.popupMenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import de.jClipCorn.gui.actionTree.ActionSource;
import de.jClipCorn.gui.actionTree.CCActionElement;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.helper.KeyStrokeUtil;

public class ActionMenuWrapper {
	private final JMenu menu;
	
	public ActionMenuWrapper(JMenu menu) {
		this.menu = menu;
	}

	public ActionMenuWrapper add(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
		}
		
		return add(el);
	}
	
	public ActionMenuWrapper add(CCActionElement el) {
		JMenuItem item = menu.add(el.getCaption());
		
		item.setIcon(el.getSmallIcon());
		
		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			item.setAccelerator(el.getKeyStroke());
		}
		
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				el.execute(ActionSource.POPUP_MENU);
			}
		});
		
		return this;
	}
}
