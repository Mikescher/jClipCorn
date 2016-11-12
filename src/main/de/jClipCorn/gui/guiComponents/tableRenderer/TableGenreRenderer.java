package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.properties.CCProperties;

public class TableGenreRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableGenreRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		if (CCProperties.getInstance().PROP_MAINFRAME_SORT_GENRES.getValue())
			setText(((CCMovieGenreList)value).asSortedString());
		else
			setText(((CCMovieGenreList)value).asString());
    }
}
