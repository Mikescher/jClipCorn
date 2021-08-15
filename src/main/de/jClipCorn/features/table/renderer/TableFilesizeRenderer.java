package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;

public class TableFilesizeRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableFilesizeRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setText(((CCFileSize)value).getFormatted());
    }
}
