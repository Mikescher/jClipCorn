package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;

public class TableDateListRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableDateListRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		CCDateTimeList d = ((CCDateTimeList)value);
		if (d.isEmptyOrOnlyUnspecified()) {
			setText(" - "); //$NON-NLS-1$
		} else {
			switch (ccprops().PROP_SERIES_DISPLAYED_DATE.getValue()) {
			case AVERAGE:
				setText(d.getAverageDateOrInvalid().toStringUINormal());
				break;
			case FIRST_VIEWED:
				setText(d.getFirstDateOrInvalid().toStringUINormal());
				break;
			case LAST_VIEWED:
				setText(d.getLastDateOrInvalid().toStringUINormal());
				break;
			default:
				setText("??_ERR_??"); //$NON-NLS-1$
				break;
			}
		}
    }
}
