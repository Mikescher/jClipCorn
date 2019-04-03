package de.jClipCorn.gui.mainFrame.clipMenuBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.UIActionTree;
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

				newm.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						el.execute(ActionSource.MENU_BAR);
					}
				});

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

				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						el.execute(ActionSource.MENU_BAR);
					}
				});

				owner.add(mi);
			}
		}
	}

	// ##########################################################################################################################################################
}
