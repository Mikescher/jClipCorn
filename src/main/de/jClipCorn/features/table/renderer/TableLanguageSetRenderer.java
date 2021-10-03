package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;

import javax.swing.*;

public class TableLanguageSetRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195711739L;

	public TableLanguageSetRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setHorizontalAlignment(SwingConstants.CENTER);
		setIcon(((CCDBLanguageSet)value).getIcon());
    }
}
