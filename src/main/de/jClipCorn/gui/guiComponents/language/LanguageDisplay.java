package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;

import javax.swing.*;
import java.awt.*;

public class LanguageDisplay extends JPanel {
	private static final long serialVersionUID = 2017286148720080714L;

	private CCDBLanguageSet value = CCDBLanguageSet.EMPTY;

	public LanguageDisplay() {
		super();
		init();
		update();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 1));
	}

	private void update() {
		removeAll();

		for (CCDBLanguage lng : value.ccstream().autosort()) {
			JLabel l = new JLabel(lng.getIcon());
			l.setToolTipText(value.toOutputString());
			add(l);

			var gap = new JPanel();
			gap.setPreferredSize(new Dimension(4, 1));
			gap.setMinimumSize(new Dimension(4, 1));
			gap.setMaximumSize(new Dimension(4, 1));
			add(gap);
		}

		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}

		setToolTipText(value.toOutputString());
	}

	public CCDBLanguageSet getValue() {
		return value;
	}

	public void setValue(CCDBLanguageSet v) {
		value = v;
		update();
	}
}
