package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.gui.guiComponents.IconRefListDisplay;
import de.jClipCorn.util.stream.CCStreams;

public class LanguageSetDisplay extends IconRefListDisplay {
	private static final long serialVersionUID = 2017286148720080714L;

	private final static int ICON_WIDTH  = 16;
	private final static int ICON_HEIGHT = 16;

	private final static int GAP_X = 4;
	private final static int GAP_Y = 1;

	private CCDBLanguageSet value = CCDBLanguageSet.EMPTY;

	public LanguageSetDisplay() {
		super(ICON_WIDTH, ICON_HEIGHT, GAP_X, GAP_Y);
	}

	public CCDBLanguageSet getValue() {
		return value;
	}

	public void setValue(CCDBLanguageSet v) {
		super.setIcons(CCStreams.iterate(value = v).autosort().map(p -> new IconRefListDisplay.Entry(p.getIcon(), p.asString())).toList());
	}
}
