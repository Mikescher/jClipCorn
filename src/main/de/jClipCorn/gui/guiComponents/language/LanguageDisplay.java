package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

import javax.swing.*;
import java.awt.*;

public class LanguageDisplay extends JPanel {
	private static final long serialVersionUID = 2017286148720080714L;

	private CCDBLanguageList value = CCDBLanguageList.EMPTY;

	public LanguageDisplay() {
		super();
		init();
		update();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
	}

	private void update() {
		removeAll();

		for (CCDBLanguage lng : value.ccstream().autosort()) {
			JLabel l = new JLabel(lng.getIcon());
			l.setToolTipText(value.toOutputString());
			add(l);
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
