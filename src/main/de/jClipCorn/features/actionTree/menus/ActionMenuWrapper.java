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
import de.jClipCorn.util.listener.ActionCallbackListener;
import java.awt.*;

public class ActionMenuWrapper {
	private final JMenu menu;
	
	public ActionMenuWrapper(JMenu menu) {
		this.menu = menu;
	}

	public ActionMenuWrapper add(Component swingsrc, String actionIdent, IActionSourceObject aso, ActionCallbackListener ucl) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return this;
		}
		
		return add(swingsrc, el, aso, ucl);
	}
	
	public ActionMenuWrapper add(Component swingsrc, CCActionElement el, IActionSourceObject aso, ActionCallbackListener ucl) {
		JMenuItem item = menu.add(el.getCaption());
		
		item.setIcon(el.getSmallIcon());
		
		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			item.setAccelerator(el.getKeyStroke());
		}
		
		item.addActionListener(arg0 -> el.execute(swingsrc, ActionSource.POPUP_MENU, aso, ucl));
		
		return this;
	}
}
