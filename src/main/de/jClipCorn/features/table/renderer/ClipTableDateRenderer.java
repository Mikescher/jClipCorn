package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.util.datetime.CCDate;

public class ClipTableDateRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public ClipTableDateRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		CCDate d = ((CCDatabaseElement)value).getAddDate();

		if (d.isMinimum()) {
			setText(" - "); //$NON-NLS-1$
		} else {
			setText(d.toStringUINormal());
		}
    }
}
