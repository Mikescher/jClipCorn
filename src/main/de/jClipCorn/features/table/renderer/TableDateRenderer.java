package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.datetime.CCDate;

public class TableDateRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableDateRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		CCDate d = ((CCDate)value);

		if (d.isMinimum()) {
			setText(" - "); //$NON-NLS-1$
		} else {
			setText(d.toStringUINormal());
		}
    }
}
