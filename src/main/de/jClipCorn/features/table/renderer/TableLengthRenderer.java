package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

public class TableLengthRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableLengthRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setText(TimeIntervallFormatter.formatShort(((int)value)));
    }
}
