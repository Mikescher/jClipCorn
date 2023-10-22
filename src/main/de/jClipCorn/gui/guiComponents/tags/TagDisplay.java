package de.jClipCorn.gui.guiComponents.tags;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.gui.guiComponents.IconRefListDisplay;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;

public class TagDisplay extends IconRefListDisplay {
	private static final long serialVersionUID = 2017286148720080714L;

	private final static int ICON_WIDTH  = 16;
	private final static int ICON_HEIGHT = 16;

	private final static int GAP_X = 4;
	private final static int GAP_Y = 1;

	private CCTagList value = CCTagList.EMPTY;

	public TagDisplay() {
		super(ICON_WIDTH, ICON_HEIGHT, GAP_X, GAP_Y);
	}

	public CCTagList getValue() {
		return value;
	}

	public void setValue(CCTagList v) {
		super.setIcons(CCStreams.iterate(value = v).map(p -> new IconRefListDisplay.Entry(p.IconOn.get16x16(), v.getAsString())).toList());

		setToolTipText(value.getAsString());
	}
}
