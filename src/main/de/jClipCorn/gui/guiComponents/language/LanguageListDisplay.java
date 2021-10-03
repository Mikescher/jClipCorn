package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

import javax.swing.*;
import java.awt.*;

public class LanguageListDisplay extends JPanel {
	private static final long serialVersionUID = 2017286148720080714L;

	private CCDBLanguageList value = CCDBLanguageList.EMPTY;

	public LanguageListDisplay() {
		super();
		init();
		update();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 1));
	}

	private void update() {
		removeAll();

		for (CCDBLanguage lng : value.ccstream()) {
			JLabel l = new JLabel(lng.getIcon());
			l.setToolTipText(lng.asString());
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

	public CCDBLanguageList getValue() {
		return value;
	}

	public void setValue(CCDBLanguageList v) {
		value = v;
		update();
	}
}
