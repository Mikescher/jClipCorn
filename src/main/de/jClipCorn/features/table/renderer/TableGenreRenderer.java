package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;

public class TableGenreRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableGenreRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		if (ccprops().PROP_MAINFRAME_SORT_GENRES.getValue())
			setText(((CCGenreList)value).asSortedString());
		else
			setText(((CCGenreList)value).asString());
    }
}
