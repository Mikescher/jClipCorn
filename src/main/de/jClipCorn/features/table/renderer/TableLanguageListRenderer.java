package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

import javax.swing.*;

public class TableLanguageListRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableLanguageListRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setHorizontalAlignment(SwingConstants.LEFT);
		setIcon(((CCDBLanguageList)value).getIcon());
    }
}
