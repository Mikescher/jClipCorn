package de.jClipCorn.features.actionTree.menus;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.helper.KeyStrokeUtil;

public class ActionMenuWrapper {
	private final JMenu menu;
	
	public ActionMenuWrapper(JMenu menu) {
		this.menu = menu;
	}

	public ActionMenuWrapper add(String actionIdent, IActionSourceObject aso) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return this;
		}
		
		return add(el, aso);
	}
	
	public ActionMenuWrapper add(CCActionElement el, IActionSourceObject aso) {
		JMenuItem item = menu.add(el.getCaption());
		
		item.setIcon(el.getSmallIcon());
		
		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			item.setAccelerator(el.getKeyStroke());
		}
		
		item.addActionListener(arg0 -> el.execute(ActionSource.POPUP_MENU, aso));
		
		return this;
	}
}
