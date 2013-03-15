package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;

public class TableGenreRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableGenreRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieGenreList)value).asString());
    }
}
