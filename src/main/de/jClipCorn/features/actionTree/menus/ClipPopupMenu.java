package de.jClipCorn.features.actionTree.menus;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.helper.KeyStrokeUtil;

public abstract class ClipPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = -3924972933691119441L;

	public ClipPopupMenu() {
		super();
	}
	
	protected abstract void init();

	protected abstract IActionSourceObject getSourceObject();

	protected JMenuItem addAction(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return null;
		}
		
		JMenuItem item = add(el.getCaption());
		
		item.setIcon(el.getSmallIcon());
		
		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			item.setAccelerator(el.getKeyStroke());
		}
		
		item.addActionListener(arg0 -> el.execute(ActionSource.POPUP_MENU, getSourceObject()));
		
		return item;
	}
	
	protected ActionMenuWrapper addActionMenuTree(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return null;
		}
		
		JMenu item = new JMenu(el.getCaption());
		add(item);
		
		item.setIcon(el.getSmallIcon());
		
		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			item.setAccelerator(el.getKeyStroke());
		}
		
		item.addActionListener(arg0 -> el.execute(ActionSource.POPUP_MENU, getSourceObject()));
		
		ActionMenuWrapper amw = new ActionMenuWrapper(item);
		
		for (CCActionElement child : el.getAllChildren()) {
			amw.add(child, getSourceObject());
		}
		
		return amw;
	}

	@SuppressWarnings("nls")
	protected void addOpenInBrowserAction(CCDatabaseElement src, CCOnlineReferenceList ref) {
		if (!ref.hasAdditional()) {
			addAction("ShowInBrowser");
			return;
		}

		final CCActionElement el0 = CCActionTree.getInstance().find("ShowInBrowser");
		if (el0 == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", "ShowInBrowser")); //$NON-NLS-1$
			return;
		}
		
		JMenu menu = new JMenu(el0.getCaption());
		add(menu);
		menu.setIcon(el0.getSmallIcon());
		
		for (CCSingleOnlineReference soref : ref) {

			JMenuItem subitem = menu.add(soref.hasDescription() ? soref.description : soref.type.asString());
			subitem.setIcon(soref.type.getIcon16x16());
			
			if (soref == ref.Main && ! KeyStrokeUtil.isEmpty(el0.getKeyStroke())) {
				subitem.setAccelerator(el0.getKeyStroke());
			}
			
			subitem.addActionListener(arg0 -> soref.openInBrowser(src));
			
			menu.add(subitem);
			
			if (soref == ref.Main && ref.hasAdditional()) menu.addSeparator();
		}
		
	}


}
