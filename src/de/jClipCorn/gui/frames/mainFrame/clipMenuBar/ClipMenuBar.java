package de.jClipCorn.gui.frames.mainFrame.clipMenuBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.jClipCorn.gui.actionTree.CCActionElement;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.util.KeyStrokeUtil;

public class ClipMenuBar extends JMenuBar {
	private static final long serialVersionUID = -2517053613105253375L;

	public ClipMenuBar() {
		super();

		createMenuBar();
	}

	private void createMenuBar() {
		CCActionTree aTree = CCActionTree.getInstance();

		for (Iterator<CCActionElement> it = aTree.getRoot().getChildren(); it.hasNext();) {
			final CCActionElement el = it.next();

			if (el.isVisible()) {
				JMenu newm = add(new JMenu(el.getCaption()));

				newm.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						el.execute();
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
						el.execute();
					}
				});

				owner.add(mi);
			}
		}
	}

	// ##########################################################################################################################################################
}
