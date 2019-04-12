package de.jClipCorn.gui.mainFrame.clipMenuBar;

import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.jClipCorn.features.actionTree.*;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.helper.KeyStrokeUtil;

public class ClipMenuBar extends JMenuBar {
	private static final long serialVersionUID = -2517053613105253375L;

	public ClipMenuBar() {
		super();

		createMenuBar();
	}

	private void createMenuBar() {
		UIActionTree aTree = CCActionTree.getInstance();

		for (Iterator<CCActionElement> it = aTree.getRoot().getChildren(); it.hasNext();) {
			final CCActionElement el = it.next();

			if (el.isVisible()) {
				JMenu newm = add(new JMenu(el.getCaption()));

				newm.addActionListener(e -> el.execute(this, ActionSource.MAINFRAME_MENU_BAR, MainFrame.getInstance().getSelectedElement(), null));

				createSubMenu(newm, el);
			}
		}
	}

	private void createSubMenu(JMenu owner, CCActionElement oel) {
		for (Iterator<CCActionElement> it = oel.getChildren(); it.hasNext();) {
			final CCActionElement el = it.next();

			if (el.isVisible()) {
				JMenuItem mi = new JMenuItem(el.getCaption(), el.getSmallIcon());
				
				if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
					mi.setAccelerator(el.getKeyStroke());
				}

				mi.addActionListener(e -> el.execute(MainFrame.getInstance(), ActionSource.MAINFRAME_MENU_BAR, MainFrame.getInstance().getSelectedElement(), null));

				owner.add(mi);
			}
		}
	}

	// ##########################################################################################################################################################
}
