package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;

public class TableFormatRenderer extends TableRenderer {
	private static final long serialVersionUID = -7556536853620039148L;

	public TableFormatRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setText(((CCFileFormat)value).asString());
		setIcon(((CCFileFormat)value).getIcon());
    }
}
