package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.datetime.YearRange;

public class TableYearRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableYearRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setText(((YearRange)value).asString());
    }
}
