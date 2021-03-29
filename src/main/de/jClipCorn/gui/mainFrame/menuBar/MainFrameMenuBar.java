package de.jClipCorn.gui.mainFrame.menuBar;

import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.UIActionTree;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.helper.KeyStrokeUtil;

import javax.swing.*;
import java.util.Iterator;

public class MainFrameMenuBar extends JMenuBar {
	private static final long serialVersionUID = -2517053613105253375L;

	public MainFrameMenuBar() {
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
