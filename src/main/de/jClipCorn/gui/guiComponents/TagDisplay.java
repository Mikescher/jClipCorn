package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;

import javax.swing.*;
import java.awt.*;

public class TagDisplay extends JPanel {
	private static final long serialVersionUID = 2017286148720080714L;

	private CCTagList value = CCTagList.EMPTY;

	public TagDisplay() {
		super();
		init();
		update();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
	}

	private void update() {
		removeAll();

		for (CCSingleTag tag : value.ccstream()) {
			JLabel l = new JLabel(tag.getOnIcon());
			l.setToolTipText(value.getAsString());
			add(l);
		}

		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}

		setToolTipText(value.getAsString());
	}

	public CCTagList getValue() {
		return value;
	}

	public void setValue(CCTagList v) {
		value = v;
		update();
	}
}
