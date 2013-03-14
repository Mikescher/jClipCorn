package de.jClipCorn.gui.frames.mainFrame.popupMenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.jClipCorn.gui.actionTree.CCActionElement;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;

public abstract class ClipPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = -3924972933691119441L;

	public ClipPopupMenu() {
		super();
		
		init();
	}
	
	protected abstract void init();
	
	protected JMenuItem addAction(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
		}
		
		JMenuItem item = add(el.getCaption());
		
		item.setIcon(el.getSmallIcon());
		
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				el.execute();
			}
		});
		
		return item;
	}
	
	protected ActionMenuWrapper addActionMenu(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
		}
		
		JMenu item = new JMenu(el.getCaption());
		add(item);
		
		item.setIcon(el.getSmallIcon());
		
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				el.execute();
			}
		});
		
		return new ActionMenuWrapper(item);
	}
}
